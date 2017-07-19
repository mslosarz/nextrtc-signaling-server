package org.nextrtc.signalingserver.domain;

import java.util.Arrays;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.defaultString;

public class Signal {
    public static final Signal EMPTY = new Signal(Signals.EMPTY);
    public static final Signal OFFER_REQUEST = new Signal(Signals.OFFER_REQUEST);
    public static final Signal OFFER_RESPONSE = new Signal(Signals.OFFER_RESPONSE, Signals.OFFER_RESPONSE_HANDLER);
    public static final Signal ANSWER_REQUEST = new Signal(Signals.ANSWER_REQUEST);
    public static final Signal ANSWER_RESPONSE = new Signal(Signals.ANSWER_RESPONSE, Signals.ANSWER_RESPONSE_HANDLER);
    public static final Signal FINALIZE = new Signal(Signals.FINALIZE);
    public static final Signal CANDIDATE = new Signal(Signals.CANDIDATE, Signals.CANDIDATE_HANDLER);
    public static final Signal PING = new Signal(Signals.PING);
    public static final Signal LEFT = new Signal(Signals.LEFT, Signals.LEFT_HANDLER);
    public static final Signal JOIN = new Signal(Signals.JOIN, Signals.JOIN_HANDLER);
    public static final Signal CREATE = new Signal(Signals.CREATE, Signals.CREATE_HANDLER);
    public static final Signal JOINED = new Signal(Signals.JOINED);
    public static final Signal NEW_JOINED = new Signal(Signals.NEW_JOINED);
    public static final Signal CREATED = new Signal(Signals.CREATED);
    public static final Signal TEXT = new Signal(Signals.TEXT, Signals.TEXT_HANDLER);
    public static final Signal ERROR = new Signal(Signals.ERROR);
    public static final Signal END = new Signal(Signals.END);

    private static final Signal[] signals = new Signal[]{EMPTY, OFFER_REQUEST,
            OFFER_RESPONSE, ANSWER_REQUEST, ANSWER_RESPONSE, FINALIZE, CANDIDATE,
            PING, LEFT, JOIN, CREATE, JOINED, NEW_JOINED, CREATED, TEXT, ERROR, END
    };
    private final String signalName;
    private final String signalHandler;

    Signal(String signalName) {
        this(signalName, Signals.EMPTY_HANDLER);
    }

    Signal(String signalName, String signalHandler) {
        this.signalName = signalName;
        this.signalHandler = signalHandler;
    }

    public boolean is(String string) {
        return ordinaryName().equalsIgnoreCase(string);
    }

    public boolean is(Signal signal) {
        return this.equals(signal);
    }

    public String ordinaryName() {
        return signalName;
    }

    public static Signal fromString(String string) {
        String signalName = defaultString(string);
        for (Signal existing : signals) {
            if (existing.signalName.equalsIgnoreCase(signalName)) {
                return existing;
            }
        }
        return new Signal(signalName);
    }

    public static Signal byHandlerName(String string) {
        String handlerName = defaultString(string);
        Optional<Signal> signal = Arrays.stream(signals)
                .filter(s -> s.signalHandler.equals(handlerName))
                .findFirst();
        if (signal.isPresent()) {
            return signal.get();
        }
        for (Signal existing : signals) {
            if (existing.signalName.equalsIgnoreCase(handlerName)) {
                return existing;
            }
        }
        return Signal.EMPTY;
    }

    public static Signal[] values() {
        return signals;
    }

    @Override
    public String toString() {
        return signalName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Signal)) {
            return false;
        }
        Signal that = (Signal) obj;
        return signalName.equalsIgnoreCase(that.signalName);
    }

    @Override
    public int hashCode() {
        return signalName.hashCode();
    }

}
