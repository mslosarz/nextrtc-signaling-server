package org.nextrtc.signalingserver.repository;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.nextrtc.signalingserver.domain.Member;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class Members implements MemberRepository {

    private Map<String, Member> members = Maps.newConcurrentMap();

    @Override
    public Collection<String> getAllIds() {
        return members.keySet();
    }

    @Override
    public Optional<Member> findBy(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(members.get(id));
    }

    @Override
    public Member register(Member member) {
        if (!members.containsKey(member.getId())) {
            members.put(member.getId(), member);
        }
        return member;
    }

    @Override
    public void unregister(String id) {
        findBy(id).ifPresent(Member::markLeft);
        Member removed = members.remove(id);
        if (removed != null) {
            removed.getConversation().ifPresent(c -> c.left(removed));
        }
    }

    @Override
    public void close() throws IOException {
        for (Member member : members.values()) {
            try {
                member.getConnection().close();
            } catch (Exception e) {
                log.error("Problem during closing member connection " + member.getId(), e);
            }
        }
        members.clear();
    }
}
