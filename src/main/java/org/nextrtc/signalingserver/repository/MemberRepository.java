package org.nextrtc.signalingserver.repository;

import io.reactivex.Observable;
import org.nextrtc.signalingserver.domain.Member;

import java.io.Closeable;
import java.util.Collection;

public interface MemberRepository extends Closeable{
    Collection<String> getAllIds();

    Observable<Member> findBy(String id);

    Observable<Member> register(Member member);

    Observable<Member> unregister(String id);

}
