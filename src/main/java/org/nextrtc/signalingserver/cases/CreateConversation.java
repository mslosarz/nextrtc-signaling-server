package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateConversation {

	@Autowired
	private Conversations conversations;

	public void execute(InternalMessage context) {
		Conversation conversation = conversations.create(context);

		conversation.join(context.getFrom(), context);
	}

}
