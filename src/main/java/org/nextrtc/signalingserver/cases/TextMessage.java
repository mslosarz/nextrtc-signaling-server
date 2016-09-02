package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Signal;
import org.nextrtc.signalingserver.domain.Signals;
import org.springframework.stereotype.Component;

@Component(Signals.TEXT_HANDLER)
public class TextMessage implements SignalHandler {

    @Override
    public void execute(InternalMessage message) {
        InternalMessage.create()//
                .from(message.getFrom())//
                .to(message.getTo())//
                .signal(Signal.TEXT)//
                .content(message.getContent())//
                .custom(message.getCustom())//
                .build()//
                .send();
    }
}
