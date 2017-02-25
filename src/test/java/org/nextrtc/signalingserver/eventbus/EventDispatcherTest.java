package org.nextrtc.signalingserver.eventbus;

import org.junit.Test;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.api.NextRTCEvents;
import org.nextrtc.signalingserver.api.NextRTCHandler;
import org.nextrtc.signalingserver.api.annotation.NextRTCEventListener;
import org.nextrtc.signalingserver.api.dto.NextRTCEvent;
import org.nextrtc.signalingserver.domain.EventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.nextrtc.signalingserver.api.NextRTCEvents.TEXT;


@ContextConfiguration(classes = {TextHandler.class, SecondHandler.class})
public class EventDispatcherTest extends BaseTest {

    @Autowired
    private EventDispatcher dispatcher;

    @Autowired
    private List<TextHandler> handlers;

    @Test
    public void shouldHandleAllMessagesEvenIfTheyAreThrowingExceptions() {
        // given
        assertThat(handlers.size(), greaterThan(1));
        handlers.forEach(h -> h.used = false);

        // when
        dispatcher.handle(EventContext.builder()
                .type(NextRTCEvents.TEXT)
                .build());

        // then
        handlers.forEach(h -> assertThat(h.used, is(true)));
    }
}

@Component("throwingExceptionHandler")
@NextRTCEventListener(TEXT)
class TextHandler implements NextRTCHandler {

    boolean used;

    @Override
    public void handleEvent(NextRTCEvent event) {
        used = true;
        throw new RuntimeException();
    }
}

@Component("throwingExceptionHandler2")
@NextRTCEventListener(TEXT)
class SecondHandler extends TextHandler {
}