package org.nextrtc.signalingserver.api;

import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.nextrtc.signalingserver.domain.Message;
import org.nextrtc.signalingserver.domain.Server;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.websocket.*;
import java.util.Objects;
import java.util.Set;

@Log4j
@Component
public class NextRTCEndpoint {

    private static Set<NextRTCEndpoint> endpoints = Sets.newConcurrentHashSet();

    @Getter(AccessLevel.PRIVATE)
    private Server server;

    public NextRTCEndpoint() {
        NextRTCEndpoint endpoint = getEndpoint();
        endpoints.add(endpoint);
        log.info("Created " + endpoint);
        endpoints.stream()
                .map(NextRTCEndpoint::getServer)
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresent(this::setServer);
    }

    private NextRTCEndpoint getEndpoint() {
        NextRTCEndpoint manuallyConfigured = manualConfiguration(new ConfigurationBuilder());
        return manuallyConfigured == null ? this : manuallyConfigured;
    }

    protected NextRTCEndpoint manualConfiguration(final ConfigurationBuilder builder) {
        return null;
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
        log.error("Occured exception for session: " + session.getId() + ", reason: " + exception.getMessage());
        log.debug("Endpoint exception: ", exception);
        server.handleError(session, exception);
    }

    @Inject
    public void setServer(Server server) {
        log.info("Setted server: " + server + " to " + this);
        this.server = server;
    }
}
