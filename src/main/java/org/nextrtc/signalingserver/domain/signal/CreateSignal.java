package org.nextrtc.signalingserver.domain.signal;


public class CreateSignal implements Signal {

	@Override
	public boolean is(String signalName) {
		return "create".equalsIgnoreCase(signalName);
	}

}
