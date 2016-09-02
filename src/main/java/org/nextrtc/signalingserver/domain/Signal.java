package org.nextrtc.signalingserver.domain;

public enum Signal {
    EMPTY(Signals.EMPTY, Signals.EMPTY_HANDLER),
    OFFER_REQUEST(Signals.OFFER_REQUEST, Signals.OFFER_REQUEST_HANDLER),
    OFFER_RESPONSE(Signals.OFFER_RESPONSE, Signals.OFFER_RESPONSE_HANDLER),
    ANSWER_REQUEST(Signals.ANSWER_REQUEST, Signals.ANSWER_REQUEST_HANDLER),
    ANSWER_RESPONSE(Signals.ANSWER_RESPONSE, Signals.ANSWER_RESPONSE_HANDLER),
    FINALIZE(Signals.FINALIZE, Signals.FINALIZE_HANDLER),
    CANDIDATE(Signals.CANDIDATE, Signals.CANDIDATE_HANDLER),
    PING(Signals.PING, Signals.PING_HANDLER),
    LEFT(Signals.LEFT, Signals.LEFT_HANDLER),
    JOIN(Signals.JOIN, Signals.JOIN_HANDLER),
    CREATE(Signals.CREATE, Signals.CREATE_HANDLER),
    JOINED(Signals.JOINED, Signals.JOINED_HANDLER),
    CREATED(Signals.CREATED, Signals.CREATED_HANDLER),
    TEXT(Signals.TEXT, Signals.TEXT_HANDLER);

    private String signalName;
    private String signalHandler;

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
