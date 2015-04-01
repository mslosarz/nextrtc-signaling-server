package org.nextrtc.signalingserver.domain.signal;

import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Candidate extends AbstractSignal {

	@Autowired
	private Conversations conversations;

	@Override
	public String name() {
		return "candidate";
	}

	@Override
	protected void execute(InternalMessage message) {
		checkPrecondition(message, conversations.getBy(message.getFrom()));

		InternalMessage.create()//
				.from(message.getFrom())//
				.to(message.getTo())//
				.signal(this)//
				.content(message.getContent())//
				.parameters(message.getParameters())//
				.build()//
				.post();

	}

}
