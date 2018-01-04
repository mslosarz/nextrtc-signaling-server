package org.nextrtc.signalingserver.domain;

import lombok.extern.log4j.Log4j;
import org.nextrtc.signalingserver.repository.MemberRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

@Component
@Scope("singleton")
@Log4j
public class DefaultMessageSender implements MessageSender {

    private MemberRepository members;

    @Inject
    public DefaultMessageSender(MemberRepository members) {
        this.members = members;
    }

    @Override
    public void send(InternalMessage message) {
        send(message, 3);
    }

    private void send(InternalMessage message, int retry) {
        if (message.getSignal() != Signal.PING) {
            log.debug("Outgoing: " + message.transformToExternalMessage());
        }
        if (message.getSignal() == Signal.ERROR) {
            tryToSendErrorMessage(message);
            return;
        }
        Member destination = message.getTo();
        if (destination == null || !destination.getSession().isOpen()) {
            log.warn("Destination member is not set or session is closed! Message will not be send: " + message.transformToExternalMessage());
            return;
        }
        members.findBy(destination.getId()).ifPresent(member ->
                lockAndRun(message, member, retry)
        );
    }

    private void tryToSendErrorMessage(InternalMessage message) {
        try {
            Session session = message.getTo().getSession();
            synchronized (session) {
                session.getBasicRemote().sendObject(message.transformToExternalMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to send message: " + message.transformToExternalMessage(), e);
        }
    }

    private void lockAndRun(InternalMessage message, Member destination, int retry) {
        try {
            RemoteEndpoint.Basic basic = destination.getSession().getBasicRemote();
            synchronized (destination) {
                basic.sendObject(message.transformToExternalMessage());
            }
        } catch (Exception e) {
            if (retry >= 0) {
                log.warn("Retrying... " + message.transformToExternalMessage());
                send(message, --retry);
            }
            log.error("Unable to send message: " + message.transformToExternalMessage() + " error during sending!");
            throw new RuntimeException("Unable to send message: " + message.transformToExternalMessage(), e);
        }
    }
}
