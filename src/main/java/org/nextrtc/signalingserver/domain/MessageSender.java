package org.nextrtc.signalingserver.domain;

public interface MessageSender {
    void send(InternalMessage message);
}
