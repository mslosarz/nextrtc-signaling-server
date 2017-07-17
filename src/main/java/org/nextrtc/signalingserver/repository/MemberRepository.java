package org.nextrtc.signalingserver.repository;

import org.nextrtc.signalingserver.domain.Member;

import java.util.Collection;
import java.util.Optional;

public interface MemberRepository {
    Collection<String> getAllIds();

    Optional<Member> findBy(String id);

    Member register(Member member);

    void unregister(String id);

}
