package org.nextrtc.signalingserver.api.dto;


import org.nextrtc.signalingserver.domain.Connection;

public interface NextRTCMember {
    default String getId() {
        return getConnection().getId();
    }

    Connection getConnection();
}
