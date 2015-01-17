package org.nextrtc.signalingserver.domain;

import javax.websocket.Session;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Builder;

@Data
@Builder
@ToString
public class Member {

	private String id;
	private Session session;

	public Member(Session session) {
		this.session = session;
		this.id = session.getId();
	}

	Member(String id, Session session) {
		this.id = session.getId();
		this.session = session;
	}
}
