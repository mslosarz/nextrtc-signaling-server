package org.nextrtc.signalingserver.exception;

public enum Exceptions {
	MEMBER_NOT_FOUND("0001"), //

	INVALID_CONVERSATION_NAME("0101"), //
	CONVERSATION_NAME_OCCUPIED("0102"), //

	UNKNOWN_ERROR("0501"), //

	;

	private String code;

	private Exceptions(String code) {
		this.code = code;
	}

	public String getErrorCode() {
		return code;
	}

	public SignalingException exception() {
		return new SignalingException(this);
	}

	public SignalingException exception(Exception reason) {
		return new SignalingException(this, reason);
	}

}
