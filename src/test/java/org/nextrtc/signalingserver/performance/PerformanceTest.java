package org.nextrtc.signalingserver.performance;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


//@Ignore
public class PerformanceTest {
    private ExecutorService service = Executors.newFixedThreadPool(4);
    private URI uri = uri();

    private URI uri() {
        try {
            return new URI("ws://localhost:8080/signaling");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldBeAbleToOpen3000Sessions() throws Exception {
        // given
        List<Future<WebSocketClient>> clients = new ArrayList<>();
        for (int i = 0; i < 30; i++)
            clients.add(service.submit(this::open100Sessions));

        // when
        List<WebSocketClient> webClients = clients.stream().map(f -> doTry(f::get)).collect(toList());

        // then
        Thread.sleep(100000);
        webClients.forEach(w -> {
            try {
                w.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void scenario1_meshConversationWith100Participant() throws Exception {
        // given

        // when

        // then
    }

    private WebSocketClient open100Sessions() {
        WebSocketClient client = new WebSocketClient();
        MockedClient socket = new MockedClient();
        try {
            List<Future<Session>> sessions = new LinkedList<>();
            client.start();
            IntStream.range(1, 100).forEach(i -> doTry(() -> sessions.add(client.connect(socket, uri))));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return client;
    }

    private <T> T doTry(Except<T> supplier) {
        try {
            return supplier.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    interface Except<T> {
        T execute() throws Exception;
    }
}
