package org.nextrtc.signalingserver.repository;

import org.nextrtc.signalingserver.domain.Member;

import java.io.Closeable;
import java.util.Collection;
import java.util.Optional;

public interface MemberRepository extends Closeable{
    Collection<String> getAllIds();

    Optional<Member> findBy(String id);

    Member register(Member member);

    void unregister(String id);

}
