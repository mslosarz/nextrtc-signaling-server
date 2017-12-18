package org.nextrtc.signalingserver.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.nextrtc.signalingserver.domain.Message;
import org.nextrtc.signalingserver.domain.Server;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.websocket.*;

@Log4j
@Component
public class NextRTCEndpoint {

    private static NextRTCEndpoint INSTANCE;

    private static volatile NextRTCEndpoint staticManualEndpoint = null;

    @Getter(AccessLevel.PRIVATE)
    private Server server;

    public NextRTCEndpoint() {
        if (INSTANCE == null) {
            synchronized (NextRTCEndpoint.class) {
                if (INSTANCE == null && getEndpoint().getServer() != null) {
                    INSTANCE = getEndpoint();
                }
            }
        }
        this.setServer(INSTANCE == null ? null : INSTANCE.getServer());
    }

    private NextRTCEndpoint getEndpoint() {
        synchronized (NextRTCEndpoint.class) {
            if (staticManualEndpoint != null) {
                return staticManualEndpoint;
            }
            EndpointConfiguration configuration = manualConfiguration(new ConfigurationBuilder());
            if (configuration == null) {
                return this;
            }
            return staticManualEndpoint = configuration.injectContext(this);
        }
    }

    protected EndpointConfiguration manualConfiguration(final ConfigurationBuilder builder) {
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
