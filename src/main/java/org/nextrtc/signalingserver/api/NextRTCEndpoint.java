package org.nextrtc.signalingserver.api;

import com.google.common.collect.Sets;
import org.apache.log4j.Logger;
import org.nextrtc.signalingserver.domain.Message;
import org.nextrtc.signalingserver.domain.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import java.util.Set;

@Component
public class NextRTCEndpoint {

    private static final Logger log = Logger.getLogger(NextRTCEndpoint.class);
    private Server server;

    private static Set<NextRTCEndpoint> endpoints = Sets.newConcurrentHashSet();

    public NextRTCEndpoint() {
        endpoints.add(this);
        log.info("Created " + this);
        endpoints.stream().filter(e -> e.server != null).findFirst().ifPresent(s -> this.setServer(s.server));
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        log.info("Opening: " + session.getId());
        server.register(session);
    }

    @OnMessage
    public void onMessage(Message message, Session session) {
        log.info("Handling message from: " + session.getId());
        server.handle(message, session);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        log.info("Closing: " + session.getId() + " with reason: " + reason.getReasonPhrase());
        server.unregister(session, reason);
    }

    @OnError
    public void onError(Session session, Throwable exception) {
        log.info("Occured exception for session: " + session.getId());
        log.error("Endpoint exception: ", exception);
        server.handleError(session, exception);
    }

    @Autowired
    public void setServer(Server server) {
        log.info("Setted server: " + server + " to " + this);
        this.server = server;
    }
}
