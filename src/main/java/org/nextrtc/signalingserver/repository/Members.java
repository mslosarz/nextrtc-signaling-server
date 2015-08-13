package org.nextrtc.signalingserver.repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.nextrtc.signalingserver.domain.Member;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;

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
		if (!members.containsValue(member)) {
			members.put(member.getId(), member);
		}
	}

	public void unregister(String id) {
        findBy(id).ifPresent(m -> m.markLeft());
		members.remove(id);
	}
}
