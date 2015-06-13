package org.nextrtc.signalingserver.domain.signal;

import static org.nextrtc.signalingserver.api.annotation.NextRTCEvents.MEMBER_LOCAL_STREAM_CREATED;

import java.util.Optional;

import org.nextrtc.signalingserver.api.annotation.NextRTCEvents;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AnswerResponse extends AbstractSignal {

	@Autowired
	private Conversations conversations;

	@Autowired
	private Finalize finalize;

	@Override
	public String name() {
		return "answerResponse";
	}

	@Override
	protected void execute(InternalMessage message) {
		checkPrecondition(message, conversations.getBy(message.getFrom()));

		InternalMessage.create()//
				.from(message.getFrom())//
				.to(message.getTo())//
				.signal(finalize)//
				.content(message.getContent())//
				.build().post();

	}

	@Override
	protected Optional<NextRTCEvents> after() {
		return Optional.of(MEMBER_LOCAL_STREAM_CREATED);
	}

}
