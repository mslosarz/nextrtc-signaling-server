package org.nextrtc.signalingserver.exception;

import org.apache.commons.lang3.StringUtils;

import static java.lang.String.format;

public class SignalingException extends RuntimeException {

    private static final long serialVersionUID = 4171073365651049929L;

    private String customMessage;

    public SignalingException(Exceptions exception) {
        super(exception.name());
    }

    public SignalingException(Exceptions exception, Throwable t) {
        super(exception.name(), t);
    }

    public SignalingException(Exceptions exception, String customMessage) {
        super(exception.name());
        this.customMessage = customMessage;
    }


    public String getCustomMessage() {
        return StringUtils.defaultString(customMessage);
    }

    public void throwException() {
        throw this;
    }

    @Override
    public String toString() {
        return format("Signaling Exception %s [%s]", getMessage(), getCustomMessage());
    }

}
