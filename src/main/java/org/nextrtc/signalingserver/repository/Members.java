package org.nextrtc.signalingserver.repository;

import com.google.common.collect.Maps;
import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;
import org.nextrtc.signalingserver.domain.Member;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@Slf4j
@Repository
public class Members implements MemberRepository {

    private Map<String, Member> members = Maps.newConcurrentMap();

    @Override
    public Collection<String> getAllIds() {
        return members.keySet();
    }

    @Override
    public Observable<Member> findBy(String id) {
        if (id == null || !members.containsKey(id)) {
            return Observable.empty();
        }
        return Observable.just(members.get(id));
    }

    @Override
    public Observable<Member> register(Member member) {
        return Observable.just(member)
                .filter(m -> !members.containsKey(m.getId()))
                .doOnNext(m -> members.put(m.getId(), m));
    }

    @Override
    public Observable<Member> unregister(String id) {
        return findBy(id)
                .doOnNext(Member::markLeft)
                .doOnNext(this::removeMemberFromConversation)
                .map(Member::getId)
                .map(members::remove);
    }

    private void removeMemberFromConversation(Member m) {
        m.getConversation().ifPresent(c -> c.left(m));
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
