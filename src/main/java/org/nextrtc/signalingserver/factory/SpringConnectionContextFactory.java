package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.cases.connection.ConnectionContext;
import org.nextrtc.signalingserver.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringConnectionContextFactory implements ConnectionContextFactory {
    @Autowired
    private ApplicationContext context;

    @Override
    public ConnectionContext create(Member from, Member to) {
        return context.getBean(ConnectionContext.class, from, to);
    }
}
