package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.api.NextRTCEvents;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.nextrtc.signalingserver.exception.Exceptions.CONVERSATION_NOT_FOUND;
import static org.nextrtc.signalingserver.exception.Exceptions.INVALID_RECIPIENT;

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
			unregisterConversation(conversation);
			sendEventConversationDestroyed(message, conversation);
		}
        sendEventMemberLeftFrom(message, conversation);
	}

	private void unregisterConversation(Conversation conversation) {
		conversations.remove(conversation.getId());
	}

	private void sendEventMemberLeftFrom(InternalMessage message, Conversation conversation) {
        eventBus.post(NextRTCEvents.MEMBER_LEFT.basedOn(message, conversation));
	}

    private void sendEventConversationDestroyed(InternalMessage message, Conversation conversation) {
        eventBus.post(NextRTCEvents.CONVERSATION_DESTROYED.basedOn(message, conversation));
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
