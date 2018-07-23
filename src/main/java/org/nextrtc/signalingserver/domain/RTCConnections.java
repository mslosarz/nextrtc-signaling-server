package org.nextrtc.signalingserver.domain;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.nextrtc.signalingserver.cases.connection.ConnectionContext;
import org.nextrtc.signalingserver.property.NextRTCProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Scope("singleton")
public class RTCConnections implements Closeable{
    private static Table<Member, Member, ConnectionContext> connections = HashBasedTable.create();

    private ScheduledExecutorService scheduler;
    private NextRTCProperties properties;

    @Inject
    public RTCConnections(ScheduledExecutorService scheduler, NextRTCProperties properties) {
        this.scheduler = scheduler;
        this.properties = properties;
    }

    @PostConstruct
    void cleanOldConnections() {
        scheduler.scheduleWithFixedDelay(this::removeOldConnections,
                properties.getMaxConnectionSetupTime(),
                properties.getMaxConnectionSetupTime(),
                TimeUnit.SECONDS);
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


    @Override
    public void close() throws IOException {
        connections.clear();
    }
}
