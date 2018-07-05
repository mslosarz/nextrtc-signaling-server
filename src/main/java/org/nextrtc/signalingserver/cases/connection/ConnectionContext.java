package org.nextrtc.signalingserver.cases.connection;

import lombok.Getter;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.api.NextRTCEvents;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.MessageSender;
import org.nextrtc.signalingserver.domain.Signal;
import org.nextrtc.signalingserver.property.NextRTCProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Component
@Scope("prototype")
public class ConnectionContext {

    private ConnectionState state = ConnectionState.NOT_INITIALIZED;
    private ZonedDateTime lastUpdated = ZonedDateTime.now();

    private NextRTCProperties properties;
    private NextRTCEventBus bus;
    private MessageSender sender;

    private Member master;
    private Member slave;
    private List<InternalMessage> candidates = new ArrayList<>();

    public ConnectionContext(Member master, Member slave) {
        this.master = master;
        this.slave = slave;
    }


    public void process(InternalMessage message) {
        if(message.getSignal() == Signal.CANDIDATE && !is(message, ConnectionState.EXCHANGE_CANDIDATES)){
            recordCandidate(message);
        }
        if (is(message, ConnectionState.OFFER_REQUESTED)) {
            setState(ConnectionState.ANSWER_REQUESTED);
            answerRequest(message);
        } else if (is(message, ConnectionState.ANSWER_REQUESTED)) {
            setState(ConnectionState.EXCHANGE_CANDIDATES);
            finalize(message);
            sendCollectedCandidates();
        } else if (is(message, ConnectionState.EXCHANGE_CANDIDATES)) {
            exchangeCandidates(message);
        }
    }

    private void sendCollectedCandidates() {
        candidates.forEach(this::exchangeCandidates);
        candidates.clear();
    }

    private void recordCandidate(InternalMessage message) {
        candidates.add(message);
    }


    private void exchangeCandidates(InternalMessage message) {
        sender.send(message.copy()
                .signal(Signal.CANDIDATE)
                .build()
        );
    }


    private void finalize(InternalMessage message) {
        sender.send(message.copy()//
                .from(slave)//
                .to(master)//
                .signal(Signal.FINALIZE)//
                .build());
        bus.post(NextRTCEvents.MEDIA_LOCAL_STREAM_CREATED.occurFor(slave.getConnection()));
        bus.post(NextRTCEvents.MEDIA_STREAMING.occurFor(master.getConnection()));
        bus.post(NextRTCEvents.MEDIA_STREAMING.occurFor(slave.getConnection()));
    }


    private void answerRequest(InternalMessage message) {
        bus.post(NextRTCEvents.MEDIA_LOCAL_STREAM_CREATED.occurFor(master.getConnection()));
        sender.send(message.copy()//
                .from(master)//
                .to(slave)//
                .signal(Signal.ANSWER_REQUEST)//
                .build()//
        );
        bus.post(NextRTCEvents.MEDIA_LOCAL_STREAM_REQUESTED.occurFor(slave.getConnection()));
    }

    private boolean is(InternalMessage message, ConnectionState state) {
        return state.equals(this.state) && state.isValid(message);
    }

    public void begin() {
        setState(ConnectionState.OFFER_REQUESTED);
        sender.send(InternalMessage.create()//
                .from(slave)//
                .to(master)//
                .signal(Signal.OFFER_REQUEST)
                .build()//
        );
        bus.post(NextRTCEvents.MEDIA_LOCAL_STREAM_REQUESTED.occurFor(master.getConnection()));
    }

    public boolean isCurrent() {
        return lastUpdated.plusSeconds(properties.getMaxConnectionSetupTime()).isAfter(ZonedDateTime.now());
    }

    private void setState(ConnectionState state) {
        this.state = state;
        lastUpdated = ZonedDateTime.now();
    }

    @Inject
    public void setBus(NextRTCEventBus bus) {
        this.bus = bus;
    }

    @Inject
    public void setProperties(NextRTCProperties properties) {
        this.properties = properties;
    }

    @Inject
    public void setSender(MessageSender sender) {
        this.sender = sender;
    }
}
