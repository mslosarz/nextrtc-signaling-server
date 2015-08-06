package org.nextrtc.signalingserver.domain;

import java.util.List;

import org.nextrtc.signalingserver.domain.signal.Signal;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

@Component
public class SignalResolver {

	private List<Signal> signals = Lists.newArrayList(//
			Signal.PING,//
			Signal.ANSWER_REQUEST,//
			Signal.ANSWER_RESPONSE,//
			Signal.CANDIDATE,//
			Signal.FINALIZE,//
			Signal.LEFT,//
			Signal.OFFER_REQUEST,//
			Signal.OFFER_RESPONSE,//
			Signal.JOIN,//
			Signal.CREATE);

	public Signal resolve(String string) {
		for (Signal signal : signals) {
			if (signal.is(string)) {
				return signal;
			}
		}
		return Signal.EMPTY;
	}

}
