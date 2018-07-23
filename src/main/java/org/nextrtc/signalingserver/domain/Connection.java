package org.nextrtc.signalingserver.domain;

public interface Connection {
    String getId();

    boolean isOpen();

    void sendObject(Object object);

    void close();
}
