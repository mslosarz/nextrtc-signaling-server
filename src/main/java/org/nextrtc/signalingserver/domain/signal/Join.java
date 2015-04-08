package org.nextrtc.signalingserver.domain.signal;

import static com.google.common.base.Optional.of;
import static org.nextrtc.signalingserver.api.annotation.NextRTCEvents.MEMBER_JOINDED;
import static org.nextrtc.signalingserver.exception.Exceptions.CONVERSATION_NOT_FOUND;

import org.nextrtc.signalingserver.api.annotation.NextRTCEvents;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;

@Component
public class Join extends AbstractSignal {

	@Autowired
	private Conversations conversations;

	@Override
	public String name() {
		return "join";
	}

	@Override
	protected void execute(InternalMessage message) {
		checkPrecondition(message).joinMember(message.getFrom());
	}

	private Conversation checkPrecondition(InternalMessage message) {
		Optional<Conversation> found = conversations.findBy(message.getContent());
		if (!found.isPresent()) {
			throw CONVERSATION_NOT_FOUND.exception();
		}
		return found.get();
	}

	@Override
	protected Optional<NextRTCEvents> after() {
		return of(MEMBER_JOINDED);
	}

}
