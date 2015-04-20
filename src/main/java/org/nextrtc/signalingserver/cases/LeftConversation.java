package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.signal.Left;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LeftConversation {

	@Autowired
	private Left left;

	public void executeFor(Member leaving, Member recipien) {
		InternalMessage.create()//
				.from(leaving)//
				.to(recipien)//
				.signal(left)//
				.build()//
				.post();
	}

}
