package org.nextrtc.signalingserver.exception;

public enum Exceptions {
    MEMBER_NOT_FOUND,
    INVALID_RECIPIENT,
    MEMBER_IN_OTHER_CONVERSATION,

    INVALID_CONVERSATION_NAME,
    CONVERSATION_NAME_OCCUPIED,
    CONVERSATION_NOT_FOUND,

    UNKNOWN_ERROR;

    public SignalingException exception() {
        return new SignalingException(this);
    }

    public SignalingException exception(String customMesage) {
        return new SignalingException(this, customMesage);
    }

    public SignalingException exception(Exception reason) {
        return new SignalingException(this, reason);
    }

}
