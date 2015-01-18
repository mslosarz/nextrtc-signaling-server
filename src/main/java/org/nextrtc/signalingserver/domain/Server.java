package org.nextrtc.signalingserver.domain;

import static org.nextrtc.signalingserver.api.annotation.NextRTCEvents.SESSION_CLOSED;
import static org.nextrtc.signalingserver.api.annotation.NextRTCEvents.SESSION_STARTED;
import static org.nextrtc.signalingserver.api.annotation.NextRTCEvents.UNEXPECTED_SITUATION;
import static org.nextrtc.signalingserver.exception.Exceptions.MEMBER_NOT_FOUND;

import javax.websocket.CloseReason;
import javax.websocket.Session;

import org.nextrtc.signalingserver.domain.InternalMessage.InternalMessageBuilder;
import org.nextrtc.signalingserver.exception.SignalingException;
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
	private SignalResolver resolver;

	@Autowired
	@Qualifier("nextRTCEventBus")
	private EventBus eventBus;

	public void register(Session session) {
		members.register(new Member(session));
		eventBus.post(SESSION_STARTED);
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
		eventBus.post(SESSION_CLOSED);
	}

	public void handleError(Session session, Throwable exception) {
		eventBus.post(UNEXPECTED_SITUATION);
	}

}
