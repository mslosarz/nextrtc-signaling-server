package org.nextrtc.signalingserver.api;

import lombok.extern.slf4j.Slf4j;
import org.nextrtc.signalingserver.DaggerNextRTCComponent;

@Slf4j
public class ConfigurationBuilder {

    public EndpointConfiguration createDefaultEndpoint() {
        log.info("Creating default configuration....");
        return new EndpointConfiguration(DaggerNextRTCComponent.builder().build());
    }
}
