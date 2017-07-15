package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.Names;
import org.nextrtc.signalingserver.NextRTCProperties;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.PingTask;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component()
public class RegisterMember {

    @Autowired
    private NextRTCProperties properties;

    @Autowired
    private Members members;

    @Autowired
    @Qualifier(Names.SCHEDULER_NAME)
    private ScheduledExecutorService scheduler;

    @Autowired
    private ApplicationContext context;

    public void incoming(Session session) {
        members.register(context.getBean(Member.class, session, ping(session)));
    }

    private ScheduledFuture<?> ping(Session session) {
        return scheduler.scheduleAtFixedRate(new PingTask(session), 0,
                properties.getPeriod(), TimeUnit.SECONDS);
    }

}
