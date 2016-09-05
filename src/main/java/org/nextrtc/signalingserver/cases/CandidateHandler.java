package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Signals;
import org.springframework.stereotype.Component;

@Component(Signals.CANDIDATE_HANDLER)
public class CandidateHandler extends Exchange {

    @Override
    protected void exchange(InternalMessage message, Conversation conversation) {
        conversation.exchangeSignals(message);
    }
}
