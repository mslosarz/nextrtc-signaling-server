package org.nextrtc.signalingserver.domain;

import org.apache.commons.lang3.StringUtils;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

public class TestClientActor {
    private Server server;
    private String name;
    private MockedClient client;
    private Connection session;

    public TestClientActor(String name, Server server) {
        this.server = server;
        this.name = name;
        Connection connection = mock(Connection.class);
        this.client = new MockedClient(server, connection);
        when(connection.getId()).thenReturn(name);
        when(connection.isOpen()).thenReturn(true);
        doNothing().when(connection).sendObject(Mockito.argThat(client));
        this.session = connection;
    }

    @Override
    public String toString() {
        return name;
    }

    public void openSocket() {
        server.register(session);
    }

    public void closeSocket() {
        server.unregister(session, "Bye");
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

    public void sendToServer(Message msg) {
        server.handle(msg, session);
    }

    public Member asMember() {
        return new Member(session, null);
    }

    public List<Message> getMessages() {
        return client.getMessages();
    }

}
