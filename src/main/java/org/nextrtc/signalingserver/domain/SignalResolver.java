package org.nextrtc.signalingserver.domain;

import java.util.List;

import org.nextrtc.signalingserver.domain.signal.Signal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SignalResolver {

	@Autowired
	private List<Signal> signals;

	public Signal resolve(String string) {
		for (Signal signal : signals) {
			if (signal.is(string)) {
				return signal;
			}
		}
		return Signal.EMPTY;
	}

}
