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

    @Test
    public void shouldBeAbleToOpen3000Sessions() throws Exception {
        // given
        ExecutorService service = Executors.newFixedThreadPool(4);

        List<Future<WebSocketClient>> clients = new ArrayList<>();
        for (int i = 0; i < 30; i++)
            clients.add(service.submit(this::open100Sessions));

        // when
        List<WebSocketClient> webClients = clients.stream().map(f -> doTry(f::get)).collect(toList());

        // then
        Thread.sleep(1000000);
        webClients.forEach(w -> {
            try {
                w.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private WebSocketClient open100Sessions() {
        WebSocketClient client = new WebSocketClient();
        MockedClient socket = new MockedClient();
        try {
            URI uri = new URI("ws://localhost:8080/signaling");
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
