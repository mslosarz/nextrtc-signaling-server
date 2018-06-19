package org.nextrtc.signalingserver.api;


import lombok.extern.slf4j.Slf4j;
import org.nextrtc.signalingserver.NextRTCComponent;
import org.nextrtc.signalingserver.domain.MessageSender;
import org.nextrtc.signalingserver.domain.Server;
import org.nextrtc.signalingserver.domain.resolver.ManualSignalResolver;
import org.nextrtc.signalingserver.eventbus.ManualEventDispatcher;
import org.nextrtc.signalingserver.property.ManualNextRTCProperties;

@Slf4j
public class EndpointConfiguration {
    private final NextRTCComponent component;

    public EndpointConfiguration(NextRTCComponent component) {
        this.component = component;
    }

    public Server nextRTCServer() {
        return component.nextRTCServer();
    }

    public ManualNextRTCProperties nextRTCProperties() {
        return component.manualProperties();
    }

    public ManualSignalResolver signalResolver() {
        return component.manualSignalResolver();
    }

    public ManualEventDispatcher eventDispatcher() {
        return component.manualEventDispatcher();
    }

    public MessageSender messageSender() {
        return component.messageSender();
    }

}
