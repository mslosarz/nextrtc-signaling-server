package org.nextrtc.signalingserver.property;

import lombok.Getter;
import org.nextrtc.signalingserver.Names;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SpringNextRTCProperties implements NextRTCProperties {

    @Value(Names.MAX_CONNECTION_SETUP_TIME)
    private int maxConnectionSetupTime;

    @Value(Names.SCHEDULED_PERIOD)
    private int pingPeriod;

    @Value(Names.SCHEDULER_SIZE)
    private int schedulerPoolSize;

    @Value(Names.JOIN_ONLY_TO_EXISTING)
    private boolean joinOnlyToExisting;
    @Value(Names.DEFAULT_CONVERSATION_TYPE)
    private String defaultConversationType;
}
