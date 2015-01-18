package org.nextrtc.signalingserver.domain.signal;

import static com.google.common.base.Optional.absent;

import org.nextrtc.signalingserver.api.annotation.NextRTCEvents;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;

@Component
public class CreatedSignal extends AbstractSignal {
	@Override
	public String name() {
		return "created";
	}

	@Override
	protected void execute(InternalMessage message) {

	}

	@Override
	protected Optional<NextRTCEvents> before() {
		return absent();
	}

	@Override
	protected Optional<NextRTCEvents> after() {
		return absent();
	}
}
