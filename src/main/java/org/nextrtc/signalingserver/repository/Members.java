package org.nextrtc.signalingserver.repository;

import java.util.Collection;
import java.util.Map;

import org.nextrtc.signalingserver.domain.Member;
import org.springframework.stereotype.Repository;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

@Repository
public class Members {

	private Map<String, Member> members = Maps.newConcurrentMap();

	public Collection<String> getAllIds() {
		return members.keySet();
	}

	public Optional<Member> findBy(String id) {
		if (id == null) {
			return Optional.absent();
		}
		return Optional.fromNullable(members.get(id));
	}

	public void register(Member member) {
		if (!members.containsValue(member)) {
			members.put(member.getId(), member);
		}
	}

	public void unregister(String id) {
		Member member = members.get(id);
		if (member != null) {
			member.markLeft();
		}
		members.remove(id);
	}
}
