package org.nextrtc.signalingserver.domain.signal;

import static com.google.common.base.Optional.of;
import static org.nextrtc.signalingserver.api.annotation.NextRTCEvents.MEMBER_LOCAL_STREAM_CREATED;

import org.nextrtc.signalingserver.api.annotation.NextRTCEvents;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.exception.Exceptions;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;

@Component
public class OfferResponse extends AbstractSignal {

	@Autowired
	private Conversations conversations;

	@Autowired
	private AnswerRequest answerRequest;

	@Override
	public String name() {
		return "offerResponse";
	}

	@Override
	protected void execute(InternalMessage message) {
		checkPrecondition(message, conversations.getBy(message.getFrom()));

		InternalMessage.create()//
				.from(message.getFrom())//
				.to(message.getTo())//
				.signal(answerRequest)//
				.content(message.getContent())//
				.build()//
				.post();
	}

	private void checkPrecondition(InternalMessage message, Optional<Conversation> conversation) {
		if (!conversation.isPresent()) {
			throw Exceptions.CONVERSATION_NOT_FOUND.exception();
		}
		if (!conversation.get().has(message.getTo())) {
			throw Exceptions.INVALID_RECIPIENT.exception();
		}
	}

	@Override
	protected Optional<NextRTCEvents> after() {
		return of(MEMBER_LOCAL_STREAM_CREATED);
	}

}
