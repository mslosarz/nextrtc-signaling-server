package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.Names;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.PingTask;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component()
public class RegisterMember {

    @Value(Names.SCHEDULED_PERIOD)
    private int period;

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
        return scheduler.scheduleAtFixedRate(new PingTask(session), period, period, TimeUnit.SECONDS);
    }

}
