package org.nextrtc.signalingserver.domain.signal;

import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.exception.Exceptions;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;

@Component
public class AnswerResponse extends AbstractSignal {

	@Autowired
	private Conversations conversations;

	@Override
	public String name() {
		return "answerResponse";
	}

	@Override
	protected void execute(InternalMessage message) {
		checkPrecondition(message, conversations.getBy(message.getFrom()));

		InternalMessage.create()//
				.from(null)//
				.to(null)//
				.content(message.getContent())//
				.parameters(message.getParameters())//
				.build().post();

	}

	private void checkPrecondition(InternalMessage message, Optional<Conversation> conversation) {
		if (!conversation.isPresent()) {
			throw Exceptions.CONVERSATION_NOT_FOUND.exception();
		}
		if (!conversation.get().has(message.getTo())) {
			throw Exceptions.INVALID_RECIPIENT.exception();
		}
	}

}
