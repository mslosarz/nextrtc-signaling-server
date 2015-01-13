package org.nextrtc.signalingserver.domain.signal;

public interface Signal {

	public static final Signal EMPTY = new Signal() {
		@Override
		public boolean is(String signalName) {
			return false;
		}
	};

	boolean is(String string);

}
