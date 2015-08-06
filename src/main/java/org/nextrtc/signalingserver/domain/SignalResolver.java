package org.nextrtc.signalingserver.domain;

import org.springframework.stereotype.Component;

@Component
public class SignalResolver {

	public Signal resolve(String string) {
		for (Signal signal : Signal.values()) {
			if (signal.is(string)) {
				return signal;
			}
		}
		return Signal.EMPTY;
	}

}
