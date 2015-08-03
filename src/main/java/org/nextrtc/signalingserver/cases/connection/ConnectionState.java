package org.nextrtc.signalingserver.cases.connection;

import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.signal.Signal;

public abstract class ConnectionState {

	public static final ConnectionState OFFER_REQUESTED = new ConnectionState() {
		@Override
		public boolean isValid(InternalMessage message) {
			return Signal.OFFER_RESPONSE.is(message.getSignal());
		}
	};
	public static final ConnectionState ANSWER_REQUESTED = new ConnectionState() {
		@Override
		public boolean isValid(InternalMessage message) {
			return Signal.ANSWER_RESPONSE.is(message.getSignal());
		}
	};
	public static final ConnectionState SPD_EXCHANGED = new ConnectionState() {
		@Override
		public boolean isValid(InternalMessage message) {
			return Signal.ANSWER_RESPONSE.is(message.getSignal());
		}
	};

	public static final ConnectionState NOT_INITIALIZED = null;


	private ConnectionState() {
	}

	public abstract boolean isValid(InternalMessage message);
}
