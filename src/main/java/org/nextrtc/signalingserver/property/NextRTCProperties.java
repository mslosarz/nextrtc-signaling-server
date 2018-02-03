package org.nextrtc.signalingserver.property;

public interface NextRTCProperties {
    int getMaxConnectionSetupTime();

    int getPingPeriod();

    int getSchedulerPoolSize();

    boolean isJoinOnlyToExisting();

    String getDefaultConversationType();
}
