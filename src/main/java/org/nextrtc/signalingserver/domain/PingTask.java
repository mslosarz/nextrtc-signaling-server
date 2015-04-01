package org.nextrtc.signalingserver.domain;

import javax.websocket.Session;

import org.nextrtc.signalingserver.domain.signal.Ping;

public class PingTask implements Runnable {

	private Ping ping;
	private Member to;

	public PingTask(Ping ping, Session to) {
		this.ping = ping;
		this.to = new Member(to, null);
	}

	@Override
	public void run() {
		InternalMessage.create()//
				.to(to)//
				.signal(ping)//
				.build()//
				.post();
	}

}
