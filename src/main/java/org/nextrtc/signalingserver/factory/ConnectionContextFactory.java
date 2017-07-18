package org.nextrtc.signalingserver.factory;


import org.nextrtc.signalingserver.cases.connection.ConnectionContext;
import org.nextrtc.signalingserver.domain.Member;

public interface ConnectionContextFactory {
    ConnectionContext create(Member from, Member to);
}
