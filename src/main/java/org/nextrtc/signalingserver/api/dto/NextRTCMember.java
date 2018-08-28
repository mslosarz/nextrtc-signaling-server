package org.nextrtc.signalingserver.api.dto;


import org.nextrtc.signalingserver.domain.Connection;
import org.nextrtc.signalingserver.domain.InternalMessage;

public interface NextRTCMember {
    default String getId() {
        return getConnection().getId();
    }

    Connection getConnection();

    void send(InternalMessage build);
}
