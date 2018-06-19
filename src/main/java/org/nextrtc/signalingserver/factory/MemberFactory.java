package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.domain.Connection;
import org.nextrtc.signalingserver.domain.Member;

import java.util.concurrent.ScheduledFuture;

public interface MemberFactory {
    Member create(Connection connection, ScheduledFuture<?> ping);
}
