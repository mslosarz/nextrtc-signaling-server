package org.nextrtc.signalingserver.domain;

public class MediaConnection {

	private Member master;
	private Member slave;
	private State state;

	public enum State {
		OPENED, CLOSED;
	}

}
