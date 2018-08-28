package org.nextrtc.signalingserver.domain;

import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.api.NextRTCServer;
import org.nextrtc.signalingserver.api.dto.NextRTCMember;
import org.nextrtc.signalingserver.cases.SignalHandler;
import org.nextrtc.signalingserver.exception.SignalingException;
import org.nextrtc.signalingserver.factory.MemberFactory;
import org.nextrtc.signalingserver.repository.MemberRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
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
    private final MemberFactory memberFactory;

    @Inject
    public Server(NextRTCEventBus eventBus,
                  MemberRepository members,
                  SignalResolver resolver,
                  MemberFactory memberFactory,
                  CloseableContext context) {
        this.eventBus = eventBus;
        this.members = members;
        this.resolver = resolver;
        this.memberFactory = memberFactory;
        this.context = context;
    }

    public void register(Connection c) {
        Observable.just(c)
                .map(memberFactory::create)
                .map(members::register)
                .map(NextRTCMember::getConnection)
                .map(SESSION_OPENED::occurFor)
                .subscribe(
                        eventBus::post,
                        error -> sendErrorOverWebSocket(c, error)
                );
    }

    public void handle(Message message, Connection connection) {
        Observable<Pair<Signal, SignalHandler>> handlers = Observable.just(message)
                .map(Message::getSignal)
                .filter(StringUtils::isNotBlank)
                .map(resolver::resolve)
                .filter(p -> p.getValue() != null);

        Observable<Member> sender = Observable.just(connection)
                .map(Connection::getId)
                .map(members::findBy)
                .map(m -> m.orElseThrow(() -> new SignalingException(MEMBER_NOT_FOUND)));

        Observable<Member> recipient = Observable.just(message)
                .map(Message::getTo)
                .map(members::findBy)
                .filter(Optional::isPresent)
                .map(Optional::get);

        Observable<InternalMessage> internalMessage = Observable.zip(handlers, sender, recipient, (h, s, r) ->
                InternalMessage.create()
                        .from(s)
                        .to(r)
                        .content(message.getContent())
                        .custom(message.getCustom())
                        .signal(h.getKey())
                        .build()
        );

        Observable.zip(internalMessage, handlers,
                (im, h) -> {
                    log.debug("Incoming: " + message);
                    h.getValue().execute(im);
                    return im;
                })
                .map(MESSAGE::basedOn)
                .subscribe(
                        eventBus::post,
                        error -> sendErrorOverWebSocket(connection, error)
                );
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

    private void sendErrorOverWebSocket(Connection connection, Throwable e) {
        try {
            Member member = new Member(connection, null);
            member.send(InternalMessage.create()
                    .to(member)
                    .signal(Signal.ERROR)
                    .content(e.getMessage())
                    .addCustom("stackTrace", writeStackTraceToString(e))
                    .build());
        } catch (Exception resendException) {
            log.error("Something goes wrong during resend! Exception omitted", resendException);
        }
    }

    private String writeStackTraceToString(Throwable e) {
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
