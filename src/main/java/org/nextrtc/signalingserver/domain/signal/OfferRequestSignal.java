package org.nextrtc.signalingserver.domain.signal;

import org.nextrtc.signalingserver.domain.InternalMessage;
import org.springframework.stereotype.Component;

@Component
public class OfferRequestSignal extends AbstractSignal {

	@Override
	public String name() {
		return "offerRequest";
	}

	@Override
	protected void execute(InternalMessage message) {

	}
}
