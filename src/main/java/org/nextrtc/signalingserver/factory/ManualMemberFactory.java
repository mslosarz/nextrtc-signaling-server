package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.domain.Member;

import javax.inject.Inject;
import javax.websocket.Session;
import java.util.concurrent.ScheduledFuture;

public class ManualMemberFactory implements MemberFactory {

    private NextRTCEventBus eventBus;

    @Inject
    public ManualMemberFactory(NextRTCEventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public Member create(Session session, ScheduledFuture<?> ping) {
        Member member = new Member(session, ping);
        member.setEventBus(eventBus);
        return member;
    }
}
