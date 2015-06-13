package org.nextrtc.signalingserver.domain.signal;

import static org.nextrtc.signalingserver.api.annotation.NextRTCEvents.MEMBER_LEFT;
import static org.nextrtc.signalingserver.exception.Exceptions.CONVERSATION_NOT_FOUND;
import static org.nextrtc.signalingserver.exception.Exceptions.INVALID_RECIPIENT;

import java.util.Optional;

import org.nextrtc.signalingserver.api.annotation.NextRTCEvents;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Left extends AbstractSignal {

	@Override
	public String name() {
		return "left";
	}

	@Autowired
	private Conversations conversations;

	@Override
	protected void execute(InternalMessage message) {
		checkPrecondition(message, conversations.getBy(message.getFrom())).left(message.getFrom());
	}

	@Override
	protected Conversation checkPrecondition(InternalMessage message, Optional<Conversation> conversation) {
		if (!conversation.isPresent()) {
			throw CONVERSATION_NOT_FOUND.exception();
		}
		if (!conversation.get().has(message.getFrom())) {
			throw INVALID_RECIPIENT.exception();
		}
		return conversation.get();
	}

	@Override
	protected Optional<NextRTCEvents> after() {
		return Optional.of(MEMBER_LEFT);
	}

}
