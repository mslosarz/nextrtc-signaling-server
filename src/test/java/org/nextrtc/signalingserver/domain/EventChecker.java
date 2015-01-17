package org.nextrtc.signalingserver.domain;

import java.util.List;

import lombok.Getter;

import org.nextrtc.signalingserver.api.NextRTCEvent;
import org.nextrtc.signalingserver.api.NextRTCHandler;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

@Getter
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

}