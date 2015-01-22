package org.nextrtc.signalingserver.domain.signal;

import org.springframework.stereotype.Component;

@Component
public class AnswerRequest extends ResponseSignal {

	@Override
	public String name() {
		return "answerRequest";
	}

}
