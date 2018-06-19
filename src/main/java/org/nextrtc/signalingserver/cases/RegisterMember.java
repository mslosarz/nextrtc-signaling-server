package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.domain.Connection;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.MessageSender;
import org.nextrtc.signalingserver.domain.PingTask;
import org.nextrtc.signalingserver.factory.MemberFactory;
import org.nextrtc.signalingserver.property.NextRTCProperties;
import org.nextrtc.signalingserver.repository.MemberRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.nextrtc.signalingserver.api.NextRTCEvents.SESSION_OPENED;

@Component
public class RegisterMember {

    private NextRTCEventBus eventBus;
    private NextRTCProperties properties;
    private MemberRepository members;
    private ScheduledExecutorService scheduler;
    private MemberFactory factory;
    private MessageSender sender;

    @Inject
    public RegisterMember(NextRTCEventBus eventBus,
                          NextRTCProperties properties,
                          MemberRepository members,
                          ScheduledExecutorService scheduler,
                          MemberFactory factory,
                          MessageSender sender) {
        this.eventBus = eventBus;
        this.properties = properties;
        this.members = members;
        this.scheduler = scheduler;
        this.factory = factory;
        this.sender = sender;
    }

    public void incoming(Connection connection) {
        Member newMember = factory.create(connection, ping(connection));
        Member registered = members.register(newMember);
        eventBus.post(SESSION_OPENED.occurFor(registered.getConnection()));
    }

    private ScheduledFuture<?> ping(Connection connection) {
        return scheduler.scheduleAtFixedRate(new PingTask(connection, sender), 1,
                properties.getPingPeriod(), TimeUnit.SECONDS);
    }

}
