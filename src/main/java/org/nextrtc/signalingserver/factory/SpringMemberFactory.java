package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.domain.Connection;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.property.NextRTCProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;

@Component
public class SpringMemberFactory extends AbstractMemberFactory {

    private ApplicationContext context;

    @Autowired
    protected SpringMemberFactory(ApplicationContext context,
                                  NextRTCProperties properties,
                                  ScheduledExecutorService scheduler) {
        super(properties, scheduler);
        this.context = context;
    }

    @Override
    public Member create(Connection connection) {
        return context.getBean(Member.class, connection, ping(connection));
    }
}
