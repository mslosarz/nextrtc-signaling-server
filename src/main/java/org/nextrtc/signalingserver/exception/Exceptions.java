package org.nextrtc.signalingserver.exception;

public enum Exceptions {
	MEMBER_NOT_FOUND("0001");

	private String code;

	private Exceptions(String code) {
		this.code = code;
	}

	public String getErrorCode() {
		return code;
	}

}
