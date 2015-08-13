package org.nextrtc.signalingserver.cases;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.websocket.Session;

import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.PingTask;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RegisterMember {

    @Value("${nextrtc.ping_period:3}")
	private int period;

	@Autowired
	private Members members;

	@Autowired
	@Qualifier("nextRTCPingScheduler")
	private ScheduledExecutorService scheduler;

	public void incomming(Session session) {
		members.register(Member.create()//
				.session(session)//
				.ping(ping(session))//
				.build());
	}

	private ScheduledFuture<?> ping(Session session) {
		return scheduler.scheduleAtFixedRate(new PingTask(session), period, period, TimeUnit.SECONDS);
	}

}
