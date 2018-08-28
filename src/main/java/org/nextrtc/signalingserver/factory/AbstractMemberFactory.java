package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.domain.Connection;
import org.nextrtc.signalingserver.domain.PingTask;
import org.nextrtc.signalingserver.property.NextRTCProperties;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class AbstractMemberFactory implements MemberFactory {
    private NextRTCProperties properties;
    private ScheduledExecutorService scheduler;

    protected AbstractMemberFactory(NextRTCProperties properties, ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
        this.properties = properties;
    }

    protected ScheduledFuture<?> ping(Connection connection) {
        return scheduler.scheduleAtFixedRate(new PingTask(connection), 1,
                properties.getPingPeriod(), TimeUnit.SECONDS);
    }

}
