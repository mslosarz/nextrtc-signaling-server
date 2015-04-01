package org.nextrtc.signalingserver.domain.signal;

import org.springframework.stereotype.Component;

@Component
public class Ping extends OutgoingSignal {

	@Override
	public String name() {
		return "ping";
	}

}
