package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.Names;
import org.nextrtc.signalingserver.NextRTCProperties;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.PingTask;
import org.nextrtc.signalingserver.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.nextrtc.signalingserver.api.NextRTCEvents.SESSION_OPENED;

@Component
public class RegisterMember {

    @Autowired
    @Qualifier(Names.EVENT_BUS)
    private NextRTCEventBus eventBus;

    @Autowired
    private NextRTCProperties properties;

    @Autowired
    private MemberRepository members;

    @Autowired
    @Qualifier(Names.SCHEDULER_NAME)
    private ScheduledExecutorService scheduler;

    @Autowired
    private ApplicationContext context;

    public void incoming(Session session) {
        Member registered = members.register(context.getBean(Member.class, session, ping(session)));
        eventBus.post(SESSION_OPENED.occurFor(registered.getSession()));
    }

    private ScheduledFuture<?> ping(Session session) {
        return scheduler.scheduleAtFixedRate(new PingTask(session), 0,
                properties.getPeriod(), TimeUnit.SECONDS);
    }

}
