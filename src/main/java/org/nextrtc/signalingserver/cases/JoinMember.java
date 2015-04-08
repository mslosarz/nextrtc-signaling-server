package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.signal.Created;
import org.nextrtc.signalingserver.domain.signal.Joined;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JoinMember {

	@Autowired
	private Joined joined;

	@Autowired
	private Created created;

	public void sendMessageToJoining(Member sender, String id) {
		InternalMessage.create()//
				.to(sender)//
				.content(id)//
				.signal(joined)//
				.build()//
				.post();
	}

	public void sendMessageToOthers(Member sender, Member member) {
		InternalMessage.create()//
				.from(sender)//
				.to(member)//
				.signal(joined)//
				.build()//
				.post();
	}

	public void sendMessageToFirstJoined(Member sender, String id) {
		InternalMessage.create()//
				.to(sender)//
				.signal(created)//
				.content(id)//
				.build()//
				.post();
	}

}
