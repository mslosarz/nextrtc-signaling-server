package org.nextrtc.signalingserver.api;

import lombok.extern.log4j.Log4j;
import org.nextrtc.signalingserver.DaggerNextRTCComponent;

@Log4j
public class ConfigurationBuilder {

    public EndpointConfiguration createDefaultEndpoint() {
        log.info("Creating default configuration....");
        return new EndpointConfiguration(DaggerNextRTCComponent.builder().build());
    }
}
