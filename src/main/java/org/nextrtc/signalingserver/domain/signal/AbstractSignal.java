package org.nextrtc.signalingserver.domain.signal;

import java.util.Optional;

import lombok.extern.log4j.Log4j;

import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.api.annotation.NextRTCEvents;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.exception.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Log4j
public abstract class AbstractSignal implements Signal {

	@Autowired
	@Qualifier("nextRTCEventBus")
	private NextRTCEventBus eventBus;

	@Override
	public boolean is(String incomming) {
		return name().equalsIgnoreCase(incomming);
	}

	public void executeMessage(InternalMessage message) {
		postEvent(before(), message);
		try {
			// execute(message);
		} catch (Exception reason) {
			postEvent(Optional.of(error()), message);
			log.debug(reason.getMessage() + " " + message);
			throw reason;
		}
		postEvent(after(), message);
	}

	private void postEvent(Optional<NextRTCEvents> event, InternalMessage message) {
		if (event.isPresent()) {
			eventBus.post(event.get().basedOn(message));
		}
	}

	protected abstract void execute(InternalMessage message);

	protected Optional<NextRTCEvents> before() {
		return Optional.empty();
	}

	protected Optional<NextRTCEvents> after() {
		return Optional.empty();
	}

	protected NextRTCEvents error() {
		return NextRTCEvents.UNEXPECTED_SITUATION;
	}

	protected Conversation checkPrecondition(InternalMessage message, Optional<Conversation> conversation) {
		if (!conversation.isPresent()) {
			throw Exceptions.CONVERSATION_NOT_FOUND.exception();
		}
		if (!conversation.get().has(message.getTo())) {
			throw Exceptions.INVALID_RECIPIENT.exception();
		}
		return conversation.get();
	}
}
