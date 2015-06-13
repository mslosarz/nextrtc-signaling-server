package org.nextrtc.signalingserver.domain.signal;

import org.apache.commons.lang3.StringUtils;
import org.nextrtc.signalingserver.domain.InternalMessage;

public interface Signal {

	public static final String JOIN_VALUE = "join";
	public static final String CREATE_VALUE = "create";

	public static final Signal EMPTY = new Signal() {
		@Override
		public boolean is(String signalName) {
			return false;
		}

		@Override
		public void executeMessage(InternalMessage internal) {
		}

		@Override
		public String name() {
			return StringUtils.EMPTY;
		}
	};

	boolean is(String string);

	String name();

	void executeMessage(InternalMessage internal);

}
