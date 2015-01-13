package org.nextrtc.signalingserver.exception;

import static java.lang.String.format;

public class SignalingException extends RuntimeException {

	private String errorCode;

	public SignalingException(Exceptions exception) {
		super(exception.name());
		this.errorCode = exception.getErrorCode();
	}

	public SignalingException(Exceptions exception, Throwable t) {
		super(exception.name(), t);
		this.errorCode = exception.getErrorCode();
	}

	public String getErrorCode() {
		return errorCode;
	}

	@Override
	public String toString() {
		return format("Signaling Exception (CODE: %s) %s", getErrorCode(), getMessage());
	}

}
