package org.nextrtc.signalingserver.api.dto;

import org.joda.time.DateTime;
import org.nextrtc.signalingserver.api.NextRTCEvents;
import org.nextrtc.signalingserver.exception.SignalingException;

import java.util.Map;
import java.util.Optional;

public interface NextRTCEvent {

    NextRTCEvents type();

    DateTime published();

    Optional<NextRTCMember> from();

    Optional<NextRTCMember> to();

    Optional<NextRTCConversation> conversation();

    Optional<SignalingException> exception();

    Map<String, String> custom();

    Optional<String> content();

    Optional<String> reason();

}
