package org.nextrtc.signalingserver.domain;

import static org.nextrtc.signalingserver.api.annotation.NextRTCEvents.SESSION_CLOSED;
import static org.nextrtc.signalingserver.api.annotation.NextRTCEvents.SESSION_STARTED;
import static org.nextrtc.signalingserver.api.annotation.NextRTCEvents.UNEXPECTED_SITUATION;
import static org.nextrtc.signalingserver.exception.Exceptions.MEMBER_NOT_FOUND;

import javax.websocket.CloseReason;
import javax.websocket.Session;

import org.nextrtc.signalingserver.cases.RegisterMember;
import org.nextrtc.signalingserver.domain.InternalMessage.InternalMessageBuilder;
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
	private RegisterMember register;

	@Autowired
	@Qualifier("nextRTCEventBus")
	private EventBus eventBus;

	public void register(Session session) {
		session.setMaxIdleTimeout(10 * 1000); // 10 seconds
		register.executeFor(session);
		eventBus.post(SESSION_STARTED);
	}

	public void handle(Message external, Session session) {
		InternalMessage message = buildInternalMessage(external, session);
		processMessage(session, message);
	}

	private void processMessage(Session session, InternalMessage message) {
		Optional<Conversation> conversation = conversations.getBy(findMember(session));
		if (conversation.isPresent()) {
			conversation.get().process(message);
		} else {
			message.execute();
		}
	}

	private InternalMessage buildInternalMessage(Message message, Session session) {
		InternalMessageBuilder messageBld = InternalMessage.create()//
				.from(findMember(session))//
				.content(message.getContent())//
				.signal(resolver.resolve(message.getSignal()));
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
		Optional<Conversation> conversation = conversations.getBy(member);
		if (conversation.isPresent()) {
			conversation.get().left(member);
		}
		members.unregister(session.getId());
	}

	public void handleError(Session session, Throwable exception) {
		unbind(session);
		eventBus.post(UNEXPECTED_SITUATION);
	}

}
