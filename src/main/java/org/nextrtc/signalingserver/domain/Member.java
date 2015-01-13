package org.nextrtc.signalingserver.domain;

import javax.websocket.Session;

import lombok.Getter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
public class Member {

	private String id;
	private Session session;

	public Member(Session session) {
		this.session = session;
		this.id = session.getId();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()//
				.append(id)//
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Member)) {
			return false;
		}
		Member other = (Member) obj;
		return new EqualsBuilder()//
				.append(id, other.id)//
				.isEquals();
	}

}
