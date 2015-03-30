package org.nextrtc.signalingserver.domain.signal;

import static org.nextrtc.signalingserver.api.annotation.NextRTCEvents.MEMBER_LEFT;
import static org.nextrtc.signalingserver.exception.Exceptions.CONVERSATION_NOT_FOUND;
import static org.nextrtc.signalingserver.exception.Exceptions.INVALID_RECIPIENT;

import org.nextrtc.signalingserver.api.annotation.NextRTCEvents;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;

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
		Optional<Conversation> conv = conversations.getBy(message.getFrom());
		checkPrecondition(message, conv);

		Conversation conversation = conv.get();
		for (Member to : conversation.getMembersWithout(message.getFrom())) {
			InternalMessage.create()//
					.from(message.getFrom())//
					.to(to)//
					.content(message.getContent())//
					.parameters(message.getParameters())//
					.signal(this)//
					.build()//
					.post();
		}
		conversation.left(message.getFrom());

	}

	private void checkPrecondition(InternalMessage message, Optional<Conversation> conversation) {
		if (!conversation.isPresent()) {
			throw CONVERSATION_NOT_FOUND.exception();
		}
		if (!conversation.get().has(message.getFrom())) {
			throw INVALID_RECIPIENT.exception();
		}
	}

	@Override
	protected Optional<NextRTCEvents> after() {
		return Optional.of(MEMBER_LEFT);
	}

}
