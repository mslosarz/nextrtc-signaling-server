package org.nextrtc.signalingserver.domain;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.api.NextRTCServer;
import org.nextrtc.signalingserver.cases.RegisterMember;
import org.nextrtc.signalingserver.cases.SignalHandler;
import org.nextrtc.signalingserver.domain.InternalMessage.InternalMessageBuilder;
import org.nextrtc.signalingserver.exception.SignalingException;
import org.nextrtc.signalingserver.repository.MemberRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;

import static org.nextrtc.signalingserver.api.NextRTCEvents.*;
import static org.nextrtc.signalingserver.exception.Exceptions.MEMBER_NOT_FOUND;

@Slf4j
@Component
public class Server implements NextRTCServer {

    private final CloseableContext context;
    private final NextRTCEventBus eventBus;
    private final MemberRepository members;
    private final SignalResolver resolver;
    private final RegisterMember register;
    private final MessageSender sender;

    @Inject
    public Server(NextRTCEventBus eventBus,
                  MemberRepository members,
                  SignalResolver resolver,
                  RegisterMember register,
                  MessageSender sender,
                  CloseableContext context) {
        this.eventBus = eventBus;
        this.members = members;
        this.resolver = resolver;
        this.register = register;
        this.sender = sender;
        this.context = context;
    }

    public void register(Connection s) {
        doSaveExecution(s, session ->
                register.incoming(session)
        );
    }

    public void handle(Message external, Connection s) {
        doSaveExecution(s, session -> {
            Pair<Signal, SignalHandler> resolve = resolver.resolve(external.getSignal());
            InternalMessage internalMessage = buildInternalMessage(external, resolve.getKey(), session);
            eventBus.post(MESSAGE.basedOn(internalMessage));
            processMessage(resolve.getValue(), internalMessage);
        });
    }

    private void processMessage(SignalHandler handler, InternalMessage message) {
        log.debug("Incoming: " + message);
        if (handler != null) {
            handler.execute(message);
        }
    }

    private InternalMessage buildInternalMessage(Message message, Signal signal, Connection connection) {
        InternalMessageBuilder bld = InternalMessage.create()//
                .from(findMember(connection))//
                .content(message.getContent())//
                .signal(signal)//
                .custom(message.getCustom());
        members.findBy(message.getTo()).ifPresent(bld::to);
        return bld.build();
    }

    private Member findMember(Connection connection) {
        return members.findBy(connection.getId()).orElseThrow(() -> new SignalingException(MEMBER_NOT_FOUND));
    }

    public void unregister(Connection connection, String reason) {
        doSaveExecution(connection, session -> {
                    members.unregister(session.getId());
                    eventBus.post(SESSION_CLOSED.occurFor(session, reason));
                }
        );
    }


    public void handleError(Connection connection, Throwable exception) {
        doSaveExecution(connection, session -> {
                    members.unregister(session.getId());
                    eventBus.post(UNEXPECTED_SITUATION.occurFor(session, exception.getMessage()));
                }
        );
    }

    private void doSaveExecution(Connection connection, Consumer<Connection> action) {
        try {
            action.accept(connection);
        } catch (Exception e) {
            log.warn("Server will try to handle this exception and send information as normal message through websocket", e);
            sendErrorOverWebSocket(connection, e);
        }
    }

    private void sendErrorOverWebSocket(Connection session, Exception e) {
        try {
            sender.send(InternalMessage.create()
                    .to(new Member(session, null))
                    .signal(Signal.ERROR)
                    .content(e.getMessage())
                    .addCustom("stackTrace", writeStackTraceToString(e))
                    .build()
            );
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

    @Override
    public void close() throws IOException {
        context.close();
    }
}
