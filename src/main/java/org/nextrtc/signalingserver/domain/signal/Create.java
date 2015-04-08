package org.nextrtc.signalingserver.domain.signal;

import static com.google.common.base.Optional.of;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.nextrtc.signalingserver.api.annotation.NextRTCEvents.CONVERSATION_CREATED;

import org.nextrtc.signalingserver.api.annotation.NextRTCEvents;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;

@Component
public class Create extends AbstractSignal {

	@Autowired
	private Conversations conversations;

	@Override
	public String name() {
		return "create";
	}

	@Override
	protected void execute(InternalMessage message) {
		createConversation(message).joinMember(message.getFrom());
	}

	private Conversation createConversation(InternalMessage message) {
		if (!isEmpty(message.getContent())) {
			return conversations.create(message.getContent());
		}
		return conversations.create();
	}

	@Override
	protected Optional<NextRTCEvents> after() {
		return of(CONVERSATION_CREATED);
	}

}
