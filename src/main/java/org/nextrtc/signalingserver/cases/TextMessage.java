package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.Names;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Signal;
import org.nextrtc.signalingserver.domain.Signals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static org.nextrtc.signalingserver.api.NextRTCEvents.TEXT;

@Component(Signals.TEXT_HANDLER)
public class TextMessage implements SignalHandler {

    @Autowired
    @Qualifier(Names.EVENT_BUS)
    private NextRTCEventBus eventBus;

    @Override
    public void execute(InternalMessage message) {
        if (!message.getFrom().hasSameConversation(message.getTo())) {
            return;
        }
        InternalMessage.create()//
                .from(message.getFrom())//
                .to(message.getTo())//
                .signal(Signal.TEXT)//
                .content(message.getContent())//
                .custom(message.getCustom())//
                .build()//
                .send();
        eventBus.post(TEXT.basedOn(message));
    }
}
