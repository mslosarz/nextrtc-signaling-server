package org.nextrtc.signalingserver.api;


import lombok.extern.log4j.Log4j;
import org.nextrtc.signalingserver.NextRTCComponent;
import org.nextrtc.signalingserver.domain.resolver.ManualSignalResolver;
import org.nextrtc.signalingserver.eventbus.ManualEventDispatcher;
import org.nextrtc.signalingserver.property.ManualNextRTCProperties;

@Log4j
public class EndpointConfiguration {
    private final NextRTCComponent component;

    public EndpointConfiguration(NextRTCComponent component) {
        this.component = component;
    }

    public NextRTCEndpoint injectContext(NextRTCEndpoint endpoint) {
        log.info("Injecting dependencies...");
        component.inject(endpoint);
        return endpoint;
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

}
