package org.nextrtc.signalingserver.domain.signal;

import static com.google.common.base.Optional.of;
import static org.nextrtc.signalingserver.api.annotation.NextRTCEvents.MEMBER_JOINDED;
import static org.nextrtc.signalingserver.exception.Exceptions.CONVERSATION_NOT_FOUND;

import org.nextrtc.signalingserver.api.annotation.NextRTCEvents;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;

@Component
public class JoinSignal extends AbstractSignal {

	@Autowired
	private Conversations conversations;

	@Autowired
	private JoinedSignal joined;

	@Autowired
	private OfferRequest offerRequest;

	@Override
	public String name() {
		return "join";
	}

	@Override
	protected void execute(InternalMessage message) {
		Optional<Conversation> found = conversations.findBy(message.getContent());
		if (!found.isPresent()) {
			throw CONVERSATION_NOT_FOUND.exception();
		}
		Conversation conversation = found.get();

		Member sender = message.getFrom();
		conversation.joinMember(sender);

		InternalMessage.create()//
				.to(sender)//
				.content(conversation.getId())//
				.signal(joined)//
				.parameters(message.getParameters())//
				.build()//
				.post();

		for (Member member : conversation.getMembersWithout(sender)) {
			InternalMessage.create()//
					.from(sender)//
					.to(member)//
					.signal(joined)//
					.parameters(message.getParameters())//
					.build()//
					.post();
			InternalMessage.create()//
					.from(sender)//
					.to(member)//
					.signal(offerRequest)//
					.parameters(message.getParameters())//
					.build()//
					.post();
		}
	}

	@Override
	protected Optional<NextRTCEvents> after() {
		return of(MEMBER_JOINDED);
	}

}
