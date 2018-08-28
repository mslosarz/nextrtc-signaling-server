package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.domain.Connection;
import org.nextrtc.signalingserver.domain.Member;

public interface MemberFactory {
    Member create(Connection connection);
}
