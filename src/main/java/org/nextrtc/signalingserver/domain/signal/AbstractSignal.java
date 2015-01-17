package org.nextrtc.signalingserver.domain.signal;

import lombok.extern.log4j.Log4j;

import org.nextrtc.signalingserver.api.annotation.NextRTCEvents;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;

@Log4j
public abstract class AbstractSignal implements Signal {

	@Autowired
	@Qualifier("nextRTCEventBus")
	private EventBus eventBus;

	@Override
	public boolean is(String incomming) {
		return signalName().equalsIgnoreCase(incomming);
	}

	@Override
	public void executeMessage(InternalMessage message) {
		postEvent(before(), message);
		try {
			execute(message);
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

	protected abstract String signalName();

	protected abstract Optional<NextRTCEvents> before();

	protected abstract Optional<NextRTCEvents> after();

	protected NextRTCEvents error() {
		return NextRTCEvents.UNEXPECTED_SITUATION;
	}
}
