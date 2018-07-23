package org.nextrtc.signalingserver.api.dto;

import java.io.Closeable;

public interface NextRTCConversation extends Closeable {
    String getId();
}
