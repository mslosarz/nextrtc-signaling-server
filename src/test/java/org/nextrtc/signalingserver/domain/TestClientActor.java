package org.nextrtc.signalingserver.domain;

import org.apache.commons.lang3.StringUtils;
import org.mockito.Mockito;

import javax.websocket.CloseReason;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestClientActor {
    private Server server;
    private String name;
    private MockedClient client;
    private Session session;

    public TestClientActor(String name, Server server) {
        this.server = server;
        this.name = name;
        Session session = mock(Session.class);
        this.client = new MockedClient(server, session);
        when(session.getId()).thenReturn(name);
        RemoteEndpoint.Async async = mock(RemoteEndpoint.Async.class);
        when(async.sendObject(Mockito.argThat(client))).thenReturn(null);
        when(session.getAsyncRemote()).thenReturn(async);
        this.session = session;
    }

    @Override
    public String toString() {
        return name;
    }

    public void openSocket() {
        server.register(session);
    }

    public void closeSocket() {
        server.unregister(session, new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Bye"));
    }

    public void create(String conversationName, String type) {
        Map<String, String> custom = new HashMap<>();
        custom.put("type", StringUtils.defaultString(type, "MESH"));
        server.handle(Message.create()
                .signal(Signals.CREATE)
                .content(conversationName)
                .custom(custom)
                .build(), session);
    }

    public void join(String conversationName) {
        server.handle(Message.create()
                .signal(Signals.JOIN)
                .content(conversationName)
                .build(), session);
    }

    public Member asMember() {
        return new Member(session, null);
    }

    public List<Message> getMessages() {
        return client.getMessages();
    }
}
