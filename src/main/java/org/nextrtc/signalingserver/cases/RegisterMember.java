package org.nextrtc.signalingserver.cases;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.websocket.Session;

import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.PingTask;
import org.nextrtc.signalingserver.domain.signal.Ping;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class RegisterMember {

	@Autowired
	private Members members;

	@Autowired
	private Ping ping;

	@Autowired
	@Qualifier("nextRTCPingScheduler")
	private ScheduledExecutorService scheduler;

	public void executeFor(Session session) {
		members.register(Member.create()//
				.session(session)//
				.ping(ping(session))//
				.build());
	}

	private ScheduledFuture<?> ping(Session session) {
		return scheduler.scheduleAtFixedRate(new PingTask(ping, session), 9, 9, TimeUnit.SECONDS);
	}

}
