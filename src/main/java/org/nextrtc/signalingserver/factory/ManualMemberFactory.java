package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.domain.Connection;
import org.nextrtc.signalingserver.domain.Member;

import javax.inject.Inject;
import java.util.concurrent.ScheduledFuture;

public class ManualMemberFactory implements MemberFactory {

    private NextRTCEventBus eventBus;

    @Inject
    public ManualMemberFactory(NextRTCEventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public Member create(Connection connection, ScheduledFuture<?> ping) {
        Member member = new Member(connection, ping);
        member.setEventBus(eventBus);
        return member;
    }
}
