package org.nextrtc.signalingserver.domain.signal;

import org.nextrtc.signalingserver.domain.InternalMessage;
import org.springframework.stereotype.Component;

@Component
public class JoinedSignal extends AbstractSignal {

	@Override
	public String name() {
		return "joined";
	}

	@Override
	protected void execute(InternalMessage message) {

	}

}
