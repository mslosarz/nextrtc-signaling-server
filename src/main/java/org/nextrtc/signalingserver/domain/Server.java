package org.nextrtc.signalingserver.domain;

import static org.nextrtc.signalingserver.api.annotation.NextRTCEvents.SESSION_CLOSED;
import static org.nextrtc.signalingserver.api.annotation.NextRTCEvents.SESSION_STARTED;
import static org.nextrtc.signalingserver.api.annotation.NextRTCEvents.UNEXPECTED_SITUATION;
import static org.nextrtc.signalingserver.exception.Exceptions.MEMBER_NOT_FOUND;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.websocket.CloseReason;
import javax.websocket.Session;

import org.nextrtc.signalingserver.domain.InternalMessage.InternalMessageBuilder;
import org.nextrtc.signalingserver.domain.signal.Ping;
import org.nextrtc.signalingserver.exception.SignalingException;
import org.nextrtc.signalingserver.repository.Conversations;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;

@Component
public class Server {

	@Autowired
	private Members members;

	@Autowired
	private Conversations conversations;

	@Autowired
	private SignalResolver resolver;

	@Autowired
	private Ping ping;

	@Autowired
	@Qualifier("nextRTCEventBus")
	private EventBus eventBus;

	@Autowired
	@Qualifier("nextRTCPingScheduler")
	private ScheduledExecutorService scheduler;

	public void register(Session session) {
		session.setMaxIdleTimeout(10 * 1000); // 10 seconds
		members.register(Member.create()//
				.session(session)//
				.ping(ping(session))//
				.build());
		eventBus.post(SESSION_STARTED);
	}

	private ScheduledFuture<?> ping(Session session) {
		return scheduler.scheduleAtFixedRate(new PingTask(ping, session), 9, 9, TimeUnit.SECONDS);
	}

	public void handle(Message external, Session session) {
		buildInternalMessage(external, session).execute();
	}

	private InternalMessage buildInternalMessage(Message message, Session session) {
		InternalMessageBuilder messageBld = InternalMessage.create()//
				.from(findMember(session))//
				.content(message.getContent())//
				.signal(resolver.resolve(message.getSignal()))//
				.parameters(message.getParameters());
		for (Member to : members.findBy(message.getTo()).asSet()) {
			messageBld.to(to);
		}
		return messageBld.build();
	}

	private Member findMember(Session session) {
		Optional<Member> member = members.findBy(session.getId());
		if (!member.isPresent()) {
			throw new SignalingException(MEMBER_NOT_FOUND);
		}
		return member.get();
	}

	public void unregister(Session session, CloseReason reason) {
		unbind(session);
		eventBus.post(SESSION_CLOSED);
	}

	private void unbind(Session session) {
		Optional<Member> maybeMember = members.findBy(session.getId());
		if (!maybeMember.isPresent()) {
			return;
		}
		Member member = maybeMember.get();
		Optional<Conversation> maybeConvers = conversations.getBy(member);
		if (maybeConvers.isPresent()) {
			resolver.resolve("left").executeMessage(InternalMessage.create().from(member).build());
		}
		members.unregister(session.getId());
	}

	public void handleError(Session session, Throwable exception) {
		unbind(session);
		eventBus.post(UNEXPECTED_SITUATION);
	}

}
