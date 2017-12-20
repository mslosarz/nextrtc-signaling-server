package org.nextrtc.signalingserver.performance;

import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


@Ignore
public class PerformanceTest {
    private URI uri = uri();

    private URI uri() {
        try {
            return new URI("ws://localhost:8090/signaling");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void scenario1_meshConversationWith64Participant() throws Exception {
        // given
        List<Tuple<Peer>> peers = IntStream.range(0, 64)
                .mapToObj(Peer::new)
                .map(this::openSession)
                .collect(toList());

        // when
        peers.forEach(p -> p.getSocket().join("x"));

        // then
//        while (true)
            Thread.sleep(100);
    }

    private <T> Tuple<T> openSession(T socket) {
        WebSocketClient client = new WebSocketClient();
        try {
            client.start();
            client.connect(socket, uri);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new Tuple<>(client, socket);
    }
}
