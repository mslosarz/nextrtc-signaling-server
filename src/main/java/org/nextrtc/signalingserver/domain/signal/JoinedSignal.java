package org.nextrtc.signalingserver.domain.signal;

import org.springframework.stereotype.Component;

@Component
public class JoinedSignal extends ResponseSignal {

	@Override
	public String name() {
		return "joined";
	}

}
