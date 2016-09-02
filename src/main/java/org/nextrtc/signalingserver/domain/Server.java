package org.nextrtc.signalingserver.domain;

import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.tuple.Pair;
import org.nextrtc.signalingserver.cases.RegisterMember;
import org.nextrtc.signalingserver.cases.SignalHandler;
import org.nextrtc.signalingserver.domain.InternalMessage.InternalMessageBuilder;
import org.nextrtc.signalingserver.exception.SignalingException;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.CloseReason;
import javax.websocket.Session;

import static org.nextrtc.signalingserver.exception.Exceptions.MEMBER_NOT_FOUND;

@Component
@Log4j
public class Server {

    @Autowired
    private Members members;

    @Autowired
    private SignalResolver resolver;

    @Autowired
    private RegisterMember register;

    public void register(Session session) {
        register.incoming(session);
    }

    public void handle(Message external, Session session) {
        Pair<Signal, SignalHandler> resolve = resolver.resolve(external.getSignal());
        InternalMessage internalMessage = buildInternalMessage(external, resolve.getKey(), session);
        processMessage(resolve.getValue(), internalMessage);
    }

    private void processMessage(SignalHandler handler, InternalMessage message) {
        log.info("Incoming: " + message);
        if (handler != null) {
            handler.execute(message);
        }
    }

    private InternalMessage buildInternalMessage(Message message, Signal signal, Session session) {
        InternalMessageBuilder bld = InternalMessage.create()//
                .from(findMember(session))//
                .content(message.getContent())//
                .signal(signal)//
                .custom(message.getCustom());
        members.findBy(message.getTo()).ifPresent(bld::to);
        return bld.build();
    }

    private Member findMember(Session session) {
        return members.findBy(session.getId()).orElseThrow(() -> new SignalingException(MEMBER_NOT_FOUND));
    }

    public void unregister(Session session, CloseReason reason) {
        members.unregisterBy(session, reason.getReasonPhrase());
    }


    public void handleError(Session session, Throwable exception) {
        members.dropOutAfterException(session, exception.getMessage());
    }

}
