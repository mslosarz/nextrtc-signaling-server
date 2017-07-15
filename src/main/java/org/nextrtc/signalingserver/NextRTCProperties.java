package org.nextrtc.signalingserver;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class NextRTCProperties {

    @Value(Names.MAX_CONNECTION_SETUP_TIME)
    private int maxConnectionSetupTime;

    @Value(Names.SCHEDULED_PERIOD)
    private int period;

    @Value(Names.SCHEDULER_SIZE)
    private int size;
}
