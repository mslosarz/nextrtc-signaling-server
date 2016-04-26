package org.nextrtc.signalingserver.domain;

import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.nextrtc.signalingserver.api.dto.NextRTCMember;

import javax.websocket.Session;
import java.util.concurrent.ScheduledFuture;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Builder(builderMethodName = "create")
public class Member implements NextRTCMember{

	private String id;
	private Session session;

	@Getter(PRIVATE)
	private ScheduledFuture<?> ping;

	private Member(String id, Session session, ScheduledFuture<?> ping) {
		this.id = session.getId();
		this.session = session;
		this.ping = ping;
	}

	public void markLeft() {
		ping.cancel(true);
	}
	@Override
	public String toString() {
        return String.format("%s", id);
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
