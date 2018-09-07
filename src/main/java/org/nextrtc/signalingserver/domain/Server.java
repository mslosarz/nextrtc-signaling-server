package org.nextrtc.signalingserver.domain;

import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;
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

import static org.apache.commons.lang3.StringUtils.isNotBlank;
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
                .switchMap(members::register)
                .map(NextRTCMember::getConnection)
                .map(SESSION_OPENED::occurFor)
                .subscribe(
                        eventBus::post,
                        error -> sendErrorOverWebSocket(c, error)
                );
    }

    public void handle(Message message, Connection connection) {
        Observable<Pair<Signal, SignalHandler>> handlers = Observable.just(message)
                .filter(m -> isNotBlank(m.getSignal()))
                .map(Message::getSignal)
                .map(resolver::resolve)
                .filter(p -> p.getValue() != null)
                .defaultIfEmpty(SignalResolver.EMPTY);

        Observable<Member> sender = Observable.just(connection)
                .filter(c -> isNotBlank(c.getId()))
                .map(Connection::getId)
                .switchMap(members::findBy)
                .defaultIfEmpty(Member.EMPTY);

        Observable<Member> recipient = Observable.just(message)
                .filter(m -> isNotBlank(m.getTo()))
                .map(Message::getTo)
                .switchMap(members::findBy)
                .defaultIfEmpty(Member.EMPTY);

        Observable<InternalMessage> internalMessage = Observable.zip(
                handlers.map(Pair::getKey),
                sender,
                recipient,
                (signal, s, r) ->
                        InternalMessage.create()
                                .to(r)
                                .from(s)
                                .content(message.getContent())
                                .custom(message.getCustom())
                                .signal(signal)
                                .build())
                .doOnNext(m -> log.debug("Incoming: " + m));

        Observable.zip(
                internalMessage,
                handlers.map(Pair::getValue),
                (im, handler) -> {
                    if (im.getFrom() == null)
                        throw new SignalingException(MEMBER_NOT_FOUND);
                    handler.execute(im);
                    return im;
                })
                .map(MESSAGE::basedOn)
                .subscribe(
                        eventBus::post,
                        error -> sendErrorOverWebSocket(connection, error)
                );
    }

    public void unregister(Connection connection, String reason) {
        Observable.just(connection)
                .filter(c -> isNotBlank(c.getId()))
                .map(Connection::getId)
                .switchMap(members::unregister)
                .map(Member::getConnection)
                .map(conn -> SESSION_CLOSED.occurFor(conn, reason))
                .subscribe(
                        eventBus::post,
                        error -> sendErrorOverWebSocket(connection, error)
                );
    }


    public void handleError(Connection connection, Throwable exception) {
        Observable.just(connection)
                .filter(c -> isNotBlank(c.getId()))
                .map(Connection::getId)
                .switchMap(members::unregister)
                .map(Member::getConnection)
                .map(conn -> UNEXPECTED_SITUATION.occurFor(conn, exception.getMessage()))
                .subscribe(
                        eventBus::post,
                        error -> sendErrorOverWebSocket(connection, error)
                );
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
