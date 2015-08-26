package org.nextrtc.signalingserver.cases;

import static org.nextrtc.signalingserver.api.NextRTCEvents.CONVERSATION_CREATED;

import org.apache.commons.lang3.StringUtils;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CreateConversation {

	@Autowired
	@Qualifier("nextRTCEventBus")
	private NextRTCEventBus eventBus;

	@Autowired
	private Conversations conversations;

	public void execute(InternalMessage message) {
		Member creating = message.getFrom();

		Conversation conversation = createConversationUsing(message);

		conversation.join(creating);

        sendEventConversationCreatedFrom(message, conversation);
	}

    private void sendEventConversationCreatedFrom(InternalMessage message, Conversation conversation) {
        eventBus.post(CONVERSATION_CREATED.basedOn(message, conversation));
	}

	private Conversation createConversationUsing(InternalMessage message) {
		if (StringUtils.isNotBlank(message.getContent())) {
			return conversations.create(message.getContent());
		}
		return conversations.create();
	}

}
