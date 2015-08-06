package org.nextrtc.signalingserver.domain;

import javax.websocket.Session;

import org.nextrtc.signalingserver.domain.signal.Signal;

public class PingTask implements Runnable {

	private Member to;

	public PingTask(Session to) {
		this.to = Member.create().session(to).build();
	}

	@Override
	public void run() {
		InternalMessage.create()//
				.to(to)//
				.signal(Signal.PING)//
				.build()//
				.post();
	}

}
