package org.nextrtc.signalingserver.domain;

import org.nextrtc.signalingserver.EventChecker;
import org.nextrtc.signalingserver.api.NextRTCEvents;
import org.nextrtc.signalingserver.api.annotation.NextRTCEventListener;

@NextRTCEventListener({NextRTCEvents.MEDIA_LOCAL_STREAM_CREATED})
public class LocalStreamCreated2 extends EventChecker {

}
