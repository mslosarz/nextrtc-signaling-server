package org.nextrtc.signalingserver.domain.signal;

import org.nextrtc.signalingserver.domain.InternalMessage;

public class Candidate extends AbstractSignal {

	@Override
	public String name() {
		return "candidate";
	}

	@Override
	protected void execute(InternalMessage message) {

	}

}
