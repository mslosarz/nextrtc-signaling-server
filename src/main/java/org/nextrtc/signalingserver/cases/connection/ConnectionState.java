package org.nextrtc.signalingserver.cases.connection;

import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.signal.Signal;

public enum ConnectionState {

	OFFER_REQUESTED {
		@Override
		public boolean isValid(InternalMessage message) {
			return false;
		}
	},
	ANSWER_REQUESTED {
		@Override
		public boolean isValid(InternalMessage message) {
			return false;
		}
	},
	EXCHANGE_CANDIDATES {
		@Override
		public boolean isValid(InternalMessage message) {
			return Signal.CANDIDATE.is(message.getSignal());
		}
	},
	NOT_INITIALIZED {
		@Override
		public boolean isValid(InternalMessage message) {
			return false;
		}
	};


	private ConnectionState() {
	}

	public abstract boolean isValid(InternalMessage message);
}
