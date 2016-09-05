package org.nextrtc.signalingserver;

import com.google.common.collect.Lists;
import org.nextrtc.signalingserver.api.NextRTCHandler;
import org.nextrtc.signalingserver.api.dto.NextRTCEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
public class EventChecker implements NextRTCHandler {

    List<NextRTCEvent> events = Lists.newArrayList();

    @Override
    public void handleEvent(NextRTCEvent event) {
        events.add(event);
    }

    public void reset() {
        events.clear();
    }

    public NextRTCEvent get(int index) {
        return events.get(index);
    }

    public List<NextRTCEvent> getEvents() {
        return this.events;
    }
}