package org.nextrtc.signalingserver.domain;

import org.nextrtc.signalingserver.cases.connection.ConnectionContext;
import org.springframework.stereotype.Component;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

@Component
public class RTCConnections {
	private static Table<Member, Member, ConnectionContext> connections = HashBasedTable.create();

	public void put(Member from, Member to, ConnectionContext ctx) {
		connections.put(from, to, ctx);
		connections.put(to, from, ctx);
	}

	public ConnectionContext get(Member from, Member to) {
		return connections.get(from, to);
	}

}
