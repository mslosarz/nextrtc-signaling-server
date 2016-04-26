/* Copyright 2015 Sabre Holdings */
package org.nextrtc.signalingserver.api.dto;

import javax.websocket.Session;

public interface NextRTCMember {
    default String getId() {
        return getSession().getId();
    }

    Session getSession();
}
