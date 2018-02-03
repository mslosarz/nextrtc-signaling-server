package org.nextrtc.signalingserver;

public interface Names {
    String EVENT_BUS = "nextRTCEventBus";
    String EVENT_DISPATCHER = "nextRTCEventDispatcher";

    String SCHEDULER_SIZE = "${nextrtc.scheduler_size:10}";
    String SCHEDULER_NAME = "nextRTCPingScheduler";
    String SCHEDULED_PERIOD = "${nextrtc.ping_period:3}";
    String MAX_CONNECTION_SETUP_TIME = "${nextrtc.max_connection_setup_time:30}";
    String JOIN_ONLY_TO_EXISTING = "${nextrtc.join_only_to_existing:false}";

    String DEFAULT_CONVERSATION_TYPE = "${nextrtc.default_conversation_type:MESH}";
}
