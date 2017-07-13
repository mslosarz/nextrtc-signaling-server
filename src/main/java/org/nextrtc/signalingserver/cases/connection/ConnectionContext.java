package org.nextrtc.signalingserver.cases.connection;

import org.nextrtc.signalingserver.Names;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.api.NextRTCEvents;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.Signal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
@Scope("prototype")
public class ConnectionContext {

    @Value(Names.MAX_CONNECTION_SETUP_TIME)
    private int maxConnectionSetupTime;

    private ConnectionState state = ConnectionState.NOT_INITIALIZED;
    private ZonedDateTime lastUpdated = ZonedDateTime.now();

    @Autowired
    @Qualifier(Names.EVENT_BUS)
    private NextRTCEventBus bus;

    private Member master;
    private Member slave;

    public ConnectionContext(Member master, Member slave) {
        this.master = master;
        this.slave = slave;
    }


    public void process(InternalMessage message) {
        if (is(message, ConnectionState.OFFER_REQUESTED)) {
            setState(ConnectionState.ANSWER_REQUESTED);
            answerRequest(message);
        } else if (is(message, ConnectionState.ANSWER_REQUESTED)) {
            setState(ConnectionState.EXCHANGE_CANDIDATES);
            finalize(message);
        } else if (is(message, ConnectionState.EXCHANGE_CANDIDATES)) {
            exchangeCandidates(message);
        }
    }


    private void exchangeCandidates(InternalMessage message) {
        message.copy().signal(Signal.CANDIDATE).build().send();
    }


    private void finalize(InternalMessage message) {
        message.copy()//
                .from(slave)//
                .to(master)//
                .signal(Signal.FINALIZE)//
                .build()//
                .send();
        bus.post(NextRTCEvents.MEDIA_LOCAL_STREAM_CREATED.occurFor(slave.getSession()));
        bus.post(NextRTCEvents.MEDIA_STREAMING.occurFor(master.getSession()));
        bus.post(NextRTCEvents.MEDIA_STREAMING.occurFor(slave.getSession()));
    }


    private void answerRequest(InternalMessage message) {
        bus.post(NextRTCEvents.MEDIA_LOCAL_STREAM_CREATED.occurFor(master.getSession()));
        message.copy()//
                .from(master)//
                .to(slave)//
                .signal(Signal.ANSWER_REQUEST)//
                .build()//
                .send();
        bus.post(NextRTCEvents.MEDIA_LOCAL_STREAM_REQUESTED.occurFor(slave.getSession()));
    }

    private boolean is(InternalMessage message, ConnectionState state) {
        return state.equals(this.state) && state.isValid(message);
    }

    public void begin() {
        setState(ConnectionState.OFFER_REQUESTED);
        InternalMessage.create()//
                .from(slave)//
                .to(master)//
                .signal(Signal.OFFER_REQUEST)
                .build()//
                .send();
        bus.post(NextRTCEvents.MEDIA_LOCAL_STREAM_REQUESTED.occurFor(master.getSession()));
    }

    public boolean isCurrent() {
        return lastUpdated.plusSeconds(maxConnectionSetupTime).isAfter(ZonedDateTime.now());
    }

    public Member getMaster() {
        return master;
    }

    public Member getSlave() {
        return slave;
    }

    public ConnectionState getState() {
        return state;
    }

    private void setState(ConnectionState state) {
        this.state = state;
        lastUpdated = ZonedDateTime.now();
    }
}
