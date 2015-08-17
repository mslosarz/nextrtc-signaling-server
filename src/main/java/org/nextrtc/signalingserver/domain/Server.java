package org.nextrtc.signalingserver.domain;

import static org.nextrtc.signalingserver.api.annotation.NextRTCEvents.SESSION_CLOSED;
import static org.nextrtc.signalingserver.api.annotation.NextRTCEvents.SESSION_STARTED;
import static org.nextrtc.signalingserver.api.annotation.NextRTCEvents.UNEXPECTED_SITUATION;
import static org.nextrtc.signalingserver.exception.Exceptions.MEMBER_NOT_FOUND;

import java.util.Optional;

import javax.websocket.CloseReason;
import javax.websocket.Session;

import lombok.extern.log4j.Log4j;

import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.cases.*;
import org.nextrtc.signalingserver.domain.InternalMessage.InternalMessageBuilder;
import org.nextrtc.signalingserver.exception.SignalingException;
import org.nextrtc.signalingserver.repository.Conversations;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Log4j
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
	private CreateConversation create;

	@Autowired
	private JoinConversation join;

	@Autowired
	private LeftConversation left;

	@Autowired
	@Qualifier("nextRTCEventBus")
	private NextRTCEventBus eventBus;

	public void register(Session session) {
		register.incomming(session);
		eventBus.post(SESSION_STARTED);
	}

	public void handle(Message external, Session session) {
		processMessage(session, buildInternalMessage(external, session));
	}

	private void processMessage(Session session, InternalMessage message) {
        log.info("Incomming: " + message);
		Optional<Conversation> conversation = conversations.getBy(findMember(session));
		if (message.isCreate()) {
			create.execute(message);
			return;
		}
		if (message.isJoin()) {
			join.execute(message);
			return;
		}
		if (conversation.isPresent() && message.isLeft()) {
			left.execute(message);
			return;
		}
		conversation.ifPresent(c -> c.execute(message));
	}

	private InternalMessage buildInternalMessage(Message message, Session session) {
		InternalMessageBuilder bld = InternalMessage.create()//
				.from(findMember(session))//
				.content(message.getContent())//
				.signal(resolver.resolve(message.getSignal()));
		members.findBy(message.getTo()).ifPresent(to -> bld.to(to));
		return bld.build();
	}

	private Member findMember(Session session) {
		return members.findBy(session.getId()).orElseThrow(() -> new SignalingException(MEMBER_NOT_FOUND));
	}

	public void unregister(Session session, CloseReason reason) {
		unbind(session);
		eventBus.post(SESSION_CLOSED);
	}

	private void unbind(Session session) {
		members.findBy(session.getId()).ifPresent(member -> {
			conversations.getBy(member).ifPresent(conv -> conv.left(member));
		});
		members.unregister(session.getId());
	}

	public void handleError(Session session, Throwable exception) {
		unbind(session);
		eventBus.post(UNEXPECTED_SITUATION);
	}

}
