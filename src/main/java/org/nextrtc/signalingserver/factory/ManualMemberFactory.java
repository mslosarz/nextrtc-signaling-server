package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.domain.Connection;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.property.NextRTCProperties;

import javax.inject.Inject;
import java.util.concurrent.ScheduledExecutorService;

public class ManualMemberFactory extends AbstractMemberFactory {

    private NextRTCEventBus eventBus;

    @Inject
    public ManualMemberFactory(NextRTCProperties properties,
                               ScheduledExecutorService scheduler,
                               NextRTCEventBus eventBus) {
        super(properties, scheduler);
        this.eventBus = eventBus;
    }

    @Override
    public Member create(Connection connection) {
        Member member = new Member(connection, ping(connection));
        member.setEventBus(eventBus);
        return member;
    }
}
