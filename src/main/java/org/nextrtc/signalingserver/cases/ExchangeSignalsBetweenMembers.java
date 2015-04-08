package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.signal.OfferRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExchangeSignalsBetweenMembers {

	@Autowired
	private OfferRequest offerRequest;

	public void begin(Member from, Member to) {
		InternalMessage.create()//
				.from(from)//
				.to(to)//
				.signal(offerRequest)//
				.build()//
				.post();
	}
}
