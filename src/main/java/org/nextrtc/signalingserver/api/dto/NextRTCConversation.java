package org.nextrtc.signalingserver.api.dto;

import java.io.Closeable;
import java.util.Set;

public interface NextRTCConversation extends Closeable {
    String getId();

    NextRTCMember getCreator();

    Set<NextRTCMember> getMembers();
}
