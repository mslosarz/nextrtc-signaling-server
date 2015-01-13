package org.nextrtc.signalingserver.domain;

import static org.nextrtc.signalingserver.exception.Exceptions.MEMBER_NOT_FOUND;

import javax.websocket.CloseReason;
import javax.websocket.Session;

import org.nextrtc.signalingserver.domain.InternalMessage.InternalMessageBuilder;
import org.nextrtc.signalingserver.exception.SignalingException;
import org.nextrtc.signalingserver.register.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;

@Component
public class Server {

	@Autowired
	private Members members;

	public void register(Session session) {
		members.register(new Member(session));
	}

	public void handle(Message external, Session session) {
		InternalMessage internal = buildInternalMessage(external, session);

	}

	private InternalMessage buildInternalMessage(Message message, Session session) {
		InternalMessageBuilder messageBld = InternalMessage.create()//
				.from(findMember(session))//
				.withContent(message.getContent())//
				.withParams(message.getParameters());
		for (Member to : members.findBy(message.getTo()).asSet()) {
			messageBld.to(to);
		}
		return messageBld.build();
	}

	private Member findMember(Session session) {
		Optional<Member> member = members.findBy(session.getId());
		if (!member.isPresent()) {
			throw new SignalingException(MEMBER_NOT_FOUND);
		}
		return member.get();
	}

	public void unregister(Session session, CloseReason reason) {
		members.unregister(session.getId());
	}

	public void manageErrorFor(Session session, Throwable exception) {

	}

}
