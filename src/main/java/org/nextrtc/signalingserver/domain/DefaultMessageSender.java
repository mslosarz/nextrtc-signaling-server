package org.nextrtc.signalingserver.domain;

import lombok.extern.log4j.Log4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

@Component
@Scope("singleton")
@Log4j
public class DefaultMessageSender implements MessageSender {

    @Override
    public void send(InternalMessage message) {
        send(message, 3);
    }

    private void send(InternalMessage message, int retry) {
        Message messageToSend = message.transformToExternalMessage();
        if (message.getSignal() != Signal.PING) {
            log.info("Outgoing: " + toString());
        }
        Session destSession = message.getTo().getSession();
        if (!destSession.isOpen()) {
            log.warn("Session is closed! Message will not be send: ");
            return;
        }
        try {
            RemoteEndpoint.Basic basic = destSession.getBasicRemote();
            synchronized (basic) {
                basic.sendObject(messageToSend);
            }
        } catch (Exception e) {
            if (retry >= 0) {
                log.info("Retrying... " + messageToSend);
                send(message, --retry);
            }
            log.error("Unable to send message: " + messageToSend + " error during sending!");
            throw new RuntimeException("Unable to send message: " + messageToSend, e);
        }
    }
}
