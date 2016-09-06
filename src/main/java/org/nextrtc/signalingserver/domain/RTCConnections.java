package org.nextrtc.signalingserver.domain;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.nextrtc.signalingserver.Names;
import org.nextrtc.signalingserver.cases.connection.ConnectionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Scope("singleton")
public class RTCConnections {
    private static Table<Member, Member, ConnectionContext> connections = HashBasedTable.create();

    @Autowired
    @Qualifier(Names.SCHEDULER_NAME)
    private ScheduledExecutorService scheduler;

    @Value(Names.MAX_CONNECTION_SETUP_TIME)
    private int maxConnectionSetupTime;

    @PostConstruct
    void cleanOldConnections() {
        scheduler.scheduleWithFixedDelay(this::removeOldConnections, maxConnectionSetupTime, maxConnectionSetupTime, TimeUnit.SECONDS);
    }

    void removeOldConnections() {
        List<ConnectionContext> oldConnections = connections.values().stream()
                .filter(context -> !context.isCurrent())
                .collect(Collectors.toList());
        oldConnections.forEach(c -> connections.remove(c.getMaster(), c.getSlave()));
    }

    public void put(Member from, Member to, ConnectionContext ctx) {
        connections.put(from, to, ctx);
        connections.put(to, from, ctx);
    }

    public Optional<ConnectionContext> get(Member from, Member to) {
        return Optional.ofNullable(connections.get(from, to));
    }

}
