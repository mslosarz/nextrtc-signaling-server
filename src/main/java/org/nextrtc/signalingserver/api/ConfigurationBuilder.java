package org.nextrtc.signalingserver.api;

import lombok.extern.log4j.Log4j;
import org.nextrtc.signalingserver.DaggerNextRTCComponent;
import org.nextrtc.signalingserver.NextRTCComponent;

@Log4j
public class ConfigurationBuilder {
    private NextRTCComponent component;

    public ConfigurationBuilder createDefaultEndpoint() {
        log.info("Creating default configuration....");
        component = DaggerNextRTCComponent.builder().build();
        return this;
    }

    public NextRTCEndpoint build() {
        NextRTCEndpoint nextRTCEndpoint = new NextRTCEndpoint();
        log.info("Injecting dependencies...");
        component.inject(nextRTCEndpoint);
        return nextRTCEndpoint;
    }
}
