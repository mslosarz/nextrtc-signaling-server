package org.nextrtc.signalingserver;

public interface Names {
    String EVENT_BUS = "nextRTCEventBus";
    String EVENT_DISPATCHER = "nextRTCEventDispatcher";

    String SCHEDULER_SIZE = "${nextrtc.scheduler_size:10}";
    String SCHEDULER_NAME = "nextRTCPingScheduler";
    String SCHEDULED_PERIOD = "${nextrtc.ping_period:3}";
    String MAX_CONNECTION_SETUP_TIME = "${nextrtc.max_connection_setup_time:30}";
}
