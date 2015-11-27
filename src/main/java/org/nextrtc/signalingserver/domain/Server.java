package org.nextrtc.signalingserver.domain;

import lombok.extern.log4j.Log4j;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.cases.CreateConversation;
import org.nextrtc.signalingserver.cases.JoinConversation;
import org.nextrtc.signalingserver.cases.LeftConversation;
import org.nextrtc.signalingserver.cases.RegisterMember;
import org.nextrtc.signalingserver.domain.InternalMessage.InternalMessageBuilder;
import org.nextrtc.signalingserver.exception.SignalingException;
import org.nextrtc.signalingserver.repository.Conversations;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.util.Optional;

import static org.nextrtc.signalingserver.api.NextRTCEvents.*;
import static org.nextrtc.signalingserver.exception.Exceptions.MEMBER_NOT_FOUND;

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
		register.incoming(session);
        eventBus.post(SESSION_OPENED.occurFor(session));
	}

	public void handle(Message external, Session session) {
		processMessage(session, buildInternalMessage(external, session));
	}

	private void processMessage(Session session, InternalMessage message) {
        log.info("Incoming: " + message);
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
				.signal(resolver.resolve(message.getSignal()))//
				.custom(message.getCustom());
		members.findBy(message.getTo()).ifPresent(bld::to);
		return bld.build();
	}

	private Member findMember(Session session) {
		return members.findBy(session.getId()).orElseThrow(() -> new SignalingException(MEMBER_NOT_FOUND));
	}

	public void unregister(Session session, CloseReason reason) {
		unbind(session);
        eventBus.post(SESSION_CLOSED.occurFor(session, reason.getReasonPhrase()));
	}

	private void unbind(Session session) {
		members.findBy(session.getId()).ifPresent(member ->
				conversations.getBy(member).ifPresent(conversation -> left.execute(InternalMessage.create()//
                .from(member)//
                .signal(Signal.LEFT)//
                .build())));
		members.unregister(session.getId());
	}

	public void handleError(Session session, Throwable exception) {
		unbind(session);
        eventBus.post(UNEXPECTED_SITUATION.occurFor(session, exception.getMessage()));
	}

}
