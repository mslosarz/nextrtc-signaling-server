package org.nextrtc.signalingserver.eventbus;

import com.google.common.eventbus.Subscribe;
import org.junit.After;
import org.junit.Test;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.Names;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.api.NextRTCEvents;
import org.nextrtc.signalingserver.api.NextRTCHandler;
import org.nextrtc.signalingserver.api.annotation.NextRTCEventListener;
import org.nextrtc.signalingserver.api.dto.NextRTCEvent;
import org.nextrtc.signalingserver.domain.EventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.nextrtc.signalingserver.api.NextRTCEvents.SESSION_CLOSED;
import static org.nextrtc.signalingserver.api.NextRTCEvents.SESSION_OPENED;

@ContextConfiguration(classes = {T1.class, T2.class, T3.class})
public class EventBusTest extends BaseTest {

    @Autowired
    @Qualifier(Names.EVENT_BUS)
    private NextRTCEventBus eventBus;

    @Autowired
    @Qualifier("t1")
    private T1 t1;

    @Autowired
    @Qualifier("t2")
    private T2 t2;

    @Autowired
    @Qualifier("t3")
    private T3 t3;

    @Test
    public void shouldRegisterListenerWithNextRTCEventListenerAnnotation() throws InterruptedException {
        // given
        Object object = new Object();

        // when
        eventBus.post(object);

        // then
        assertThat(t1.getO(), is(object));
    }

    @Test
    public void shouldCallHandleEventMethod() throws Exception {
        // given
        NextRTCEvent event = event(SESSION_OPENED);
        NextRTCEvent notValidEvent = event(SESSION_CLOSED);

        // when
        eventBus.post(event);
        eventBus.post(notValidEvent);

        // then
        assertThat(t2.getEvent(), is(event));
        assertThat(t3.getEvent(), nullValue());
    }

    private NextRTCEvent event(final NextRTCEvents event) {
        return EventContext.builder().type(event).build();
    }

    @After
    public void resetClass() {
        t1.setO(null);
        t2.setEvent(null);
        t3.setEvent(null);
    }
}

@Component("t1")
@NextRTCEventListener
class T1 {

    private Object o;

    @Subscribe
    public void callMe(Object o) {
        this.o = o;
    }

    public Object getO() {
        return this.o;
    }

    public void setO(Object o) {
        this.o = o;
    }
}

@Component("t2")
@NextRTCEventListener(SESSION_OPENED)
class T2 implements NextRTCHandler {

    private NextRTCEvent event;

    @Override
    public void handleEvent(NextRTCEvent event) {
        this.event = event;
    }

    public NextRTCEvent getEvent() {
        return this.event;
    }

    public void setEvent(NextRTCEvent event) {
        this.event = event;
    }
}

@Component("t3")
@NextRTCEventListener
class T3 implements NextRTCHandler {

    private NextRTCEvent event;

    @Override
    public void handleEvent(NextRTCEvent event) {
        this.event = event;
    }

    public NextRTCEvent getEvent() {
        return this.event;
    }

    public void setEvent(NextRTCEvent event) {
        this.event = event;
    }
}