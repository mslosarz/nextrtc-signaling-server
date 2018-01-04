package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.domain.*;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static org.nextrtc.signalingserver.api.NextRTCEvents.TEXT;

@Component(Signals.TEXT_HANDLER)
public class TextMessage implements SignalHandler {

    private NextRTCEventBus eventBus;
    private MessageSender sender;

    @Inject
    public TextMessage(NextRTCEventBus eventBus,
                       MessageSender sender) {
        this.eventBus = eventBus;
        this.sender = sender;
    }

    @Override
    public void execute(InternalMessage message) {
        Member from = message.getFrom();
        if (message.getTo() == null && from.getConversation().isPresent()) {
            Conversation conversation = from.getConversation().get();
            conversation.broadcast(from, message);
            eventBus.post(TEXT.basedOn(message));
        } else if (from.hasSameConversation(message.getTo())) {
            sender.send(message);
            eventBus.post(TEXT.basedOn(message));
        }

    }
}
