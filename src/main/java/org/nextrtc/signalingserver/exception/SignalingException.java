package org.nextrtc.signalingserver.exception;

import org.apache.commons.lang3.StringUtils;

import static java.lang.String.format;

public class SignalingException extends RuntimeException {

	private static final long serialVersionUID = 4171073365651049929L;

	private String errorCode;
	private String customMessage;

	public SignalingException(Exceptions exception) {
		super(exception.getErrorCode() + ": " + exception.name());
		this.errorCode = exception.getErrorCode();
	}

	public SignalingException(Exceptions exception, Throwable t) {
		super(exception.getErrorCode() + ": " + exception.name(), t);
		this.errorCode = exception.getErrorCode();
	}

	public SignalingException(Exceptions exception, String customMessage) {
		super(exception.getErrorCode() + ": " + exception.name());
		this.errorCode = exception.getErrorCode();
		this.customMessage = customMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getCustomMessage(){
		return StringUtils.defaultString(customMessage);
	}

	@Override
	public String toString() {
		return format("Signaling Exception (CODE: %s) %s [%s]", getErrorCode(), getMessage(), getCustomMessage());
	}

}
