package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.domain.Connection;
import org.nextrtc.signalingserver.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;

@Component
public class SpringMemberFactory implements MemberFactory {

    @Autowired
    private ApplicationContext context;

    @Override
    public Member create(Connection connection, ScheduledFuture<?> ping) {
        return context.getBean(Member.class, connection, ping);
    }
}
