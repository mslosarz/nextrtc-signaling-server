package org.nextrtc.signalingserver.cases;

import static org.nextrtc.signalingserver.exception.Exceptions.CONVERSATION_NOT_FOUND;
import static org.nextrtc.signalingserver.exception.Exceptions.INVALID_RECIPIENT;

import java.util.Optional;

import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.api.annotation.NextRTCEvents;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class LeftConversation {

	@Autowired
	@Qualifier("nextRTCEventBus")
	private NextRTCEventBus eventBus;

	@Autowired
	private Conversations conversations;

	public void execute(InternalMessage message) {
		Conversation conversation = checkPrecondition(message, conversations.getBy(message.getFrom()));
		conversation.left(message.getFrom());
		if (conversation.isWithoutMember()) {
			conversations.remove(conversation.getId());
		}
		sendEventMemberLeftFrom(message);
	}

	private void sendEventMemberLeftFrom(InternalMessage message) {
		eventBus.post(NextRTCEvents.MEMBER_LEFT.basedOn(message));
	}

	protected Conversation checkPrecondition(InternalMessage message, Optional<Conversation> conversation) {
		if (!conversation.isPresent()) {
			throw CONVERSATION_NOT_FOUND.exception();
		}
		if (!conversation.get().has(message.getFrom())) {
			throw INVALID_RECIPIENT.exception();
		}
		return conversation.get();
	}

}
