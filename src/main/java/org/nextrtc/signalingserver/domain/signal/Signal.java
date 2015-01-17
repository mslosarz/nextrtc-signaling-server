package org.nextrtc.signalingserver.domain.signal;

import org.nextrtc.signalingserver.domain.InternalMessage;

public interface Signal {

	public static final Signal EMPTY = new Signal() {
		@Override
		public boolean is(String signalName) {
			return false;
		}

		@Override
		public void executeMessage(InternalMessage internal) {
		}
	};

	boolean is(String string);

	void executeMessage(InternalMessage internal);

}
