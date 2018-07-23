package org.nextrtc.signalingserver.performance;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.stream.Collectors.toList;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


@Ignore
@Slf4j
@RunWith(Parameterized.class)
public class PerformanceTest {

    @Parameters(name = "{0}: on url {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                //{"RatPack", uri("localhost:5050")},
                {"Spring", uri("localhost:8080")}//,{"Standalone", uri("localhost:8090")}
        });
    }

    private URI uri;

    private static URI uri(String uri) {
        try {
            return new URI(format("wss://%s/signaling", uri));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PerformanceTest(String type, URI uri) {
        this.uri = uri;
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
        peers.stream()
                .map(Tuple::getSocket)
                .forEach(p -> await()
                        .atMost(1, MINUTES)
                        .until(() -> p.getJoined().size() == size - 1));
        result.forEach(p -> assertThat(p.getName() + ": Some members were missed", p.getJoined(), containsInAnyOrder(without(allNames, p.getName()))));
        result.forEach(p -> assertThat(p.getName() + " doesn't have all participant!", p.getJoined(), hasSize(size - 1)));
        result.forEach(p -> assertThat(p.getName() + " has joined to himself!", p.getJoined(), not(containsInAnyOrder(p.getName()))));
        result.forEach(p -> await()
                .atMost(1, MINUTES)
                .until(() -> p.getCandidates().size() == size - 1));
        result.forEach(p -> assertThat(p.getName() + " didn't exchange all candidates", p.getCandidates().size(), equalTo(size - 1)));
        result.forEach(p -> assertThat(" there are errors for " + p.getName() + ": " + p.getErrors(), p.getErrors(), hasSize(0)));
        peers.forEach(this::stop);
    }

    @Test
    public void scenario2_broadcastConversationWith64Participant() throws Exception {
        // given
        int size = 64;
        List<Tuple<Peer>> peers = IntStream.range(0, size + 1)
                .mapToObj(Peer::new)
                .map(this::openSession)
                .collect(toList());

        // when
        Tuple<Peer> masterTuple = peers.remove(0);
        Peer master = masterTuple.getSocket();
        master.createConv("broadcast");
        await()
                .atMost(1, MINUTES)
                .until(() -> master.getJoinedTo() != null);

        peers.parallelStream()
                .map(Tuple::getSocket)
                .forEach(s -> s.join("broadcast"));

        peers.stream()
                .map(Tuple::getSocket)
                .forEach(p -> await()
                        .atMost(1, MINUTES)
                        .until(() -> p.getJoinedTo() != null));

        await()
                .atMost(1, MINUTES)
                .until(() -> master.getCandidates().size() == size);

        // then
        assertThat(master.getOfferRequests().size(), is(size));
        assertThat(master.getFinalized().size(), is(size));
        assertThat(master.getCandidates().size(), is(size));
        List<Peer> result = peers.stream().map(Tuple::getSocket).collect(toList());
        List<String> allNames = result.stream().map(Peer::getName).collect(toList());
        assertThat(master.getCandidates().size(), is(size));
        master.getCandidates().forEach((k, v) -> assertThat(allNames.contains(k), is(true)));
        result.forEach(p -> {
            assertThat(p.getJoinedTo(), is("broadcast"));
            assertThat(p.getJoined().size(), is(1));
            assertThat(p.getCandidates().size(), is(1));
            assertThat(p.getCandidates().keySet(), containsInAnyOrder(master.getName()));
        });

        peers.forEach(this::stop);
        stop(masterTuple);
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
