package org.nextrtc.signalingserver.performance;

import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.stream.Collectors.toList;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


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
        int size = 64;
        List<Tuple<Peer>> peers = IntStream.range(0, size)
                .mapToObj(Peer::new)
                .map(this::openSession)
                .collect(toList());

        // when
        peers.parallelStream()
                .map(Tuple::getSocket)
                .forEach(s -> s.join("x"));
        peers.stream()
                .map(Tuple::getSocket)
                .forEach(p -> await()
                        .atMost(1, MINUTES)
                        .until(() -> p.getJoinedTo() != null));

        // then
        List<Peer> result = peers.stream().map(Tuple::getSocket).collect(toList());
        List<String> allNames = result.stream().map(Peer::getName).collect(toList());
        result.forEach(p -> assertThat(p.getName() + ": Some members were missed", p.getJoined(), containsInAnyOrder(without(allNames, p.getName()))));
        peers.stream()
                .map(Tuple::getSocket)
                .forEach(p -> await()
                        .atMost(1, MINUTES)
                        .until(() -> p.getJoined().size() == size - 1));
        result.forEach(p -> assertThat(p.getName() + " doesn't have all participant!", p.getJoined(), hasSize(size - 1)));
        result.forEach(p -> assertThat(p.getName() + " has joined to himself!", p.getJoined(), not(containsInAnyOrder(p.getName()))));
//        result.forEach(p -> await()
//                        .atMost(1, MINUTES)
//                        .until(() -> p.getCandidates().size() == size - 1));
        Thread.sleep(10 * 1000);
        result.forEach(p -> assertThat(p.getName() + " didn't exchange all candidates", p.getCandidates().size(), equalTo(size - 1)));
        result.forEach(p -> assertThat(" there are errors for " + p.getName() + ": " + p.getErrors(), p.getErrors(), hasSize(0)));
        peers.forEach(this::stop);
    }

    private String[] without(List<String> allNames, String name) {
        List<String> mod = new ArrayList<>(allNames);
        mod.remove(name);
        return mod.toArray(new String[0]);
    }

    private void stop(Tuple<Peer> p) {
        try {
            p.getClient().stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
