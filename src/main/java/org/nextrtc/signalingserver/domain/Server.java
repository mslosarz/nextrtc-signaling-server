package org.nextrtc.signalingserver.domain;

import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.tuple.Pair;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.cases.RegisterMember;
import org.nextrtc.signalingserver.cases.SignalHandler;
import org.nextrtc.signalingserver.domain.InternalMessage.InternalMessageBuilder;
import org.nextrtc.signalingserver.exception.SignalingException;
import org.nextrtc.signalingserver.repository.MemberRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;

import static org.nextrtc.signalingserver.api.NextRTCEvents.SESSION_CLOSED;
import static org.nextrtc.signalingserver.api.NextRTCEvents.UNEXPECTED_SITUATION;
import static org.nextrtc.signalingserver.exception.Exceptions.MEMBER_NOT_FOUND;

@Log4j
@Component
public class Server {

    private NextRTCEventBus eventBus;
    private MemberRepository members;
    private SignalResolver resolver;
    private RegisterMember register;

    @Inject
    public Server(NextRTCEventBus eventBus,
                  MemberRepository members,
                  SignalResolver resolver,
                  RegisterMember register) {
        this.eventBus = eventBus;
        this.members = members;
        this.resolver = resolver;
        this.register = register;
    }

    public void register(Session s) {
        doSaveExecution(s, session ->
                register.incoming(session)
        );
    }

    public void handle(Message external, Session s) {
        doSaveExecution(s, session -> {
            Pair<Signal, SignalHandler> resolve = resolver.resolve(external.getSignal());
            InternalMessage internalMessage = buildInternalMessage(external, resolve.getKey(), session);
            processMessage(resolve.getValue(), internalMessage);
        });
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

    public void unregister(Session s, CloseReason reason) {
        doSaveExecution(s, session -> {
                    members.unregister(session.getId());
                    eventBus.post(SESSION_CLOSED.occurFor(session, reason.getReasonPhrase()));
                }
        );
    }


    public void handleError(Session s, Throwable exception) {
        doSaveExecution(new SessionWrapper(s), session -> {
                    members.unregister(session.getId());
                    eventBus.post(UNEXPECTED_SITUATION.occurFor(session, exception.getMessage()));
                }
        );
    }

    private void doSaveExecution(Session session, Consumer<Session> action) {
        try {
            action.accept(session);
        } catch (Exception e) {
            log.warn("Server will try to handle this exception and send information as normal message through websocket", e);
            sendErrorOverWebSocket(session, e);
        }
    }

    private void sendErrorOverWebSocket(Session session, Exception e) {
        try {
            InternalMessage.create()
                    .to(new Member(session, null))
                    .signal(Signal.ERROR)
                    .content(e.getMessage())
                    .addCustom("stackTrace", writeStackTraceToString(e))
                    .build()
                    .send();
        } catch (Exception resendException) {
            log.error("Something goes wrong during resend! Exception omitted", resendException);
        }
    }

    private String writeStackTraceToString(Exception e) {
        if (log.isDebugEnabled()) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            return errors.toString();
        }
        return e.getClass().getSimpleName() + " - " + e.getMessage();
    }

}
