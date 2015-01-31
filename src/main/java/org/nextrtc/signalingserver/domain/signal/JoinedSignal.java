package org.nextrtc.signalingserver.domain.signal;

import org.springframework.stereotype.Component;

@Component
public class JoinedSignal extends OutgoingSignal {

	@Override
	public String name() {
		return "joined";
	}

}
