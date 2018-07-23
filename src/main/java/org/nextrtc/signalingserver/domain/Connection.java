package org.nextrtc.signalingserver.domain;

import java.io.IOException;

public interface Connection {
    String getId();

    boolean isOpen();

    void sendObject(Object object);

    void close() throws IOException;
}
