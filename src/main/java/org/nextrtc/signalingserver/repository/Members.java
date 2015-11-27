package org.nextrtc.signalingserver.repository;

import com.google.common.collect.Maps;
import org.nextrtc.signalingserver.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
public class Members {

	private Map<String, Member> members = Maps.newConcurrentMap();

	public Collection<String> getAllIds() {
		return members.keySet();
	}

	public Optional<Member> findBy(String id) {
		if (id == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(members.get(id));
	}

	public void register(Member member) {
		members.computeIfAbsent(member.getId(), put -> member);
	}

	public void unregister(String id) {
        findBy(id).ifPresent(Member::markLeft);
		members.remove(id);
	}
}
