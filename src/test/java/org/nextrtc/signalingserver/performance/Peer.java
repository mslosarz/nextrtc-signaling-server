package org.nextrtc.signalingserver.performance;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.nextrtc.signalingserver.domain.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.String.format;
import static java.util.Collections.synchronizedList;
import static org.awaitility.Awaitility.await;
import static org.nextrtc.signalingserver.domain.Message.create;

@WebSocket
@Getter
public class Peer {
    private final static ExecutorService service = Executors.newCachedThreadPool();
    private final Gson gson = new GsonBuilder().create();
    private Session session;
    private String name;
    private String joinedTo;
    private List<Message> offerRequests = synchronizedList(new ArrayList<>());
    private List<Message> answerRequests = synchronizedList(new ArrayList<>());
    private List<Message> finalized = synchronizedList(new ArrayList<>());
    private Map<String, Action> actions = new ConcurrentHashMap<>();
    private Map<String, List<String>> candidates = new ConcurrentHashMap<>();
    private List<String> joined = synchronizedList(new ArrayList<>());
    private List<String> errors = synchronizedList(new ArrayList<>());
    private List<Message> log = synchronizedList(new ArrayList<>());

    public Peer(int i) {
        actions.put("created", (s, msg) -> {
            name = msg.getTo();
            joinedTo = msg.getContent();
        });
        actions.put("joined", (s, msg) -> {
            name = msg.getTo();
            joinedTo = msg.getContent();
        });
        actions.put("offerrequest", (s, msg) -> {
            offerRequests.add(msg);
            send(create()
                    .to(msg.getFrom())
                    .signal("offerResponse")
                    .content("offer from" + name)
                    .build());
        });
        actions.put("answerrequest", (s, msg) -> {
            answerRequests.add(msg);
            send(create()
                    .to(msg.getFrom())
                    .signal("answerResponse")
                    .content("answer from" + name)
                    .build());
        });
        actions.put("finalize", (s, msg) -> {
            finalized.add(msg);
            send(create()
                    .to(msg.getFrom())
                    .signal("candidate")
                    .content("local candidate from " + name)
                    .build());
            send(create()
                    .to(msg.getFrom())
                    .signal("candidate")
                    .content("remote candidate from " + name)
                    .build());
        });
        actions.put("candidate", (s, msg) -> {
            candidates.computeIfAbsent(msg.getFrom(), k -> new ArrayList<>());
            candidates.get(msg.getFrom()).add(msg.getContent());
            if (!msg.getContent().contains("answer")) {
                send(create()
                        .to(msg.getFrom())
                        .signal("candidate")
                        .content("answer from " + name + " on " + msg.getContent())
                        .build());
            }
        });
        actions.put("ping", (s, m) -> {
        });
        actions.put("newjoined", (s, msg) -> {
            joined.add(msg.getContent());
        });
        actions.put("error", (s, msg) -> {
            errors.add(msg.getContent());
        });
        actions.put("end", (s, msg) -> {
            throw new RuntimeException(msg.getContent());
        });
    }

    public void join(String name) {
        send(create()
                .signal("join")
                .content(name)
                .build());
    }

    public void createConv(String name) {
        Map<String, String> custom = new HashMap<>();
        custom.put("type", "BROADCAST");
        send(create()
                .signal("create")
                .content(name)
                .custom(custom)
                .build());
    }

    public void leave() {
        send(create()
                .signal("left")
                .build());
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        Message message = gson.fromJson(msg, Message.class);
        if (!"ping".equalsIgnoreCase(message.getSignal())) {
            log.add(message);
        }
        service.submit(() -> {
            String key = message.getSignal().toLowerCase();
            if (actions.containsKey(key)) {
                actions.get(key).execute(session, message);
            }
        });
    }

    @OnWebSocketClose
    public void onClose(int status, String reason) {
        session = null;
        log.add(create().content(reason).build());
    }

    private void send(Message message) {
        log.add(message);
        service.submit(() -> {
            await().until(() -> getSession() != null);
            try {
                session.getRemote().sendStringByFuture(gson.toJson(message));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private interface Action {
        void execute(Session session, Message message);
    }

    @Override
    public String toString() {
        return format("%s joinedTo %s, received information about joining from %s participant. " +
                        "Received candidates from %s persons. Has %s errors",
                name, getJoinedTo(), getJoined().size(), getCandidates().size(), errors.size());
    }
}
