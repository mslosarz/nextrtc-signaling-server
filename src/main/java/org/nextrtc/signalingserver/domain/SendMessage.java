package org.nextrtc.signalingserver.domain;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SendMessage {

    private final Connection connection;
    private final InternalMessage message;
    private final int tries;

    public SendMessage(Connection connection, InternalMessage message, int numberOfTries) {
        this.connection = connection;
        this.message = message;
        this.tries = numberOfTries;
    }

    private void send(InternalMessage message, int tries) {
        if (message.getTo() == null) {
            throw new RuntimeException("Recipient not defined");
        }
        if (message.getSignal() != Signal.PING) {
            log.debug("Outgoing: " + message.transformToExternalMessage());
        }
        if (message.getSignal() == Signal.ERROR) {
            tryToSendErrorMessage(message);
            return;
        }
        if (!connection.isOpen()) {
            log.warn("Destination member is not set or session is closed! Message will not be send: " + message.transformToExternalMessage());
            return;
        }
        lockAndRun(message, tries);
    }

    private void tryToSendErrorMessage(InternalMessage message) {
        try {
            synchronized (connection) {
                connection.sendObject(message.transformToExternalMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to send message: " + message.transformToExternalMessage(), e);
        }
    }

    private void lockAndRun(InternalMessage message, int retry) {
        try {
            synchronized (connection) {
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

    public void send() {
        send(message, tries);
    }
}
