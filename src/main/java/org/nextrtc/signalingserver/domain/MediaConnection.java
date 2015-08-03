package org.nextrtc.signalingserver.domain;

public class MediaConnection {

	private Member master;
	private Member slave;
	private State state = State.NOT_INITIALIZED;

	public enum State {
		NOT_INITIALIZED, OPENED, CLOSED;
	}

}
