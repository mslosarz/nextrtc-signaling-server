package org.nextrtc.signalingserver.domain;

import javax.websocket.Session;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Builder;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Data
@Builder(builderMethodName = "create")
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

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Member)) {
			return false;
		}
		Member m = (Member) o;
		return new EqualsBuilder()//
				.append(m.id, id)//
				.isEquals();

	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()//
				.append(id)//
				.build();
	}

}
