package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.signal.Signal;
import org.springframework.stereotype.Component;

@Component
public class LeftMember {

	public void executeFor(Member leaving, Member recipien) {
		InternalMessage.create()//
				.from(leaving)//
				.to(recipien)//
				.signal(Signal.LEFT)//
				.build()//
				.post();
	}

}
