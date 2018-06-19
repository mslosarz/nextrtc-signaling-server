package org.nextrtc.signalingserver.domain;

import lombok.extern.slf4j.Slf4j;
import org.nextrtc.signalingserver.repository.MemberRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope("singleton")
@Slf4j
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
        if (destination == null || !destination.getConnection().isOpen()) {
            log.warn("Destination member is not set or session is closed! Message will not be send: " + message.transformToExternalMessage());
            return;
        }
        members.findBy(destination.getId()).ifPresent(member ->
                lockAndRun(message, member, retry)
        );
    }

    private void tryToSendErrorMessage(InternalMessage message) {
        try {
            Connection connection = message.getTo().getConnection();
            synchronized (connection) {
                connection.sendObject(message.transformToExternalMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to send message: " + message.transformToExternalMessage(), e);
        }
    }

    private void lockAndRun(InternalMessage message, Member destination, int retry) {
        try {
            Connection connection = destination.getConnection();
            synchronized (destination) {
                connection.sendObject(message.transformToExternalMessage());
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
