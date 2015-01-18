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

	@Override
	public String name() {
		return "offerResponse";
	}

	@Override
	protected void execute(InternalMessage message) {
		Optional<Conversation> optional = conversations.getBy(message.getFrom());
		checkPrecondition(message, optional);

	}

	private void checkPrecondition(InternalMessage message, Optional<Conversation> optional) {
		if (!optional.isPresent()) {
			throw Exceptions.INVALID_RECIPIENT.exception();
		}
		if (!optional.get().has(message.getTo())) {
			throw Exceptions.INVALID_RECIPIENT.exception();
		}
	}

	@Override
	protected Optional<NextRTCEvents> after() {
		return of(MEMBER_LOCAL_STREAM_CREATED);
	}

}
