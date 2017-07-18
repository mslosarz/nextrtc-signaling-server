package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.domain.Member;

import javax.websocket.Session;
import java.util.concurrent.ScheduledFuture;

public interface MemberFactory {
    Member create(Session session, ScheduledFuture<?> ping);
}
