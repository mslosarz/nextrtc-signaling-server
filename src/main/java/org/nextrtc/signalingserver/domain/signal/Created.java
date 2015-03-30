package org.nextrtc.signalingserver.domain.signal;

import org.springframework.stereotype.Component;

@Component
public class Created extends OutgoingSignal {

	@Override
	public String name() {
		return "created";
	}

}
