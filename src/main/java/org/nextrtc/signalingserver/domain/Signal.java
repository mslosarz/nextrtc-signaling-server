package org.nextrtc.signalingserver.domain;

public enum Signal {
    EMPTY(Signals.EMPTY),
    OFFER_REQUEST(Signals.OFFER_REQUEST),
    OFFER_RESPONSE(Signals.OFFER_RESPONSE, Signals.OFFER_RESPONSE_HANDLER),
    ANSWER_REQUEST(Signals.ANSWER_REQUEST),
    ANSWER_RESPONSE(Signals.ANSWER_RESPONSE, Signals.ANSWER_RESPONSE_HANDLER),
    FINALIZE(Signals.FINALIZE),
    CANDIDATE(Signals.CANDIDATE, Signals.CANDIDATE_HANDLER),
    PING(Signals.PING),
    LEFT(Signals.LEFT, Signals.LEFT_HANDLER),
    JOIN(Signals.JOIN, Signals.JOIN_HANDLER),
    CREATE(Signals.CREATE, Signals.CREATE_HANDLER),
    JOINED(Signals.JOINED),
    NEW_JOINED(Signals.NEW_JOINED),
    CREATED(Signals.CREATED),
    TEXT(Signals.TEXT, Signals.TEXT_HANDLER),
    ERROR(Signals.ERROR),
    END(Signals.END);

    private String signalName;
    private String signalHandler;

    Signal(String signalName) {
        this.signalName = signalName;
        this.signalHandler = Signals.EMPTY_HANDLER;
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

    public String handlerName() {
        return signalHandler;
    }
}
