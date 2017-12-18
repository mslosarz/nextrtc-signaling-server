package org.nextrtc.signalingserver.performance;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;


@Ignore
public class PerformanceTest {

    @Test
    public void shouldConnectToServer() throws Exception {
        // given
        WebSocketClient client = new WebSocketClient();
        MockedClient socket = new MockedClient();
        URI uri = new URI("ws://localhost:8080/signaling");
        try {
            client.start();
            Session session = client.connect(socket, uri).get();
        } finally {
            client.stop();
        }
        // when

        // then

    }
}
