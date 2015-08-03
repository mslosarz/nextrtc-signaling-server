package org.nextrtc.signalingserver.domain.signal;

import org.apache.commons.lang3.StringUtils;

public interface Signal {

	public static final String JOIN_VALUE = "join";
	public static final String CREATE_VALUE = "create";
	public static final String OFFER_REQUEST_VALUE = "offerRequest";
	public static final String OFFER_RESPONSE_VALUE = "offerResponse";
	public static final String ANSWER_REQUEST_VALUE = "answerRequest";
	public static final String ANSWER_RESPONSE_VALUE = "answerResponse";
	public static final String FINALIZE_VALUE = "finalize";
	public static final String CANDIDATE_VALUE = "candidate";
	public static final String LEFT_VALUE = "left";

	public static final Signal EMPTY = new Signal() {
		@Override
		public String name() {
			return StringUtils.EMPTY;
		}
	};
	public static final Signal OFFER_REQUEST = new Signal() {
		@Override
		public String name() {
			return OFFER_REQUEST_VALUE;
		}
	};
	public static final Signal OFFER_RESPONSE = new Signal() {
		@Override
		public String name() {
			return OFFER_RESPONSE_VALUE;
		}
	};
	public static final Signal ANSWER_REQUEST = new Signal() {
		@Override
		public String name() {
			return ANSWER_REQUEST_VALUE;
		}
	};
	public static final Signal ANSWER_RESPONSE = new Signal() {
		@Override
		public String name() {
			return ANSWER_RESPONSE_VALUE;
		}
	};
	public static final Signal FINALIZE = new Signal() {
		@Override
		public String name() {
			return FINALIZE_VALUE;
		}
	};
	public static final Signal CANDIDATE = new Signal() {
		@Override
		public String name() {
			return CANDIDATE_VALUE;
		}
	};

	default boolean is(String string) {
		return name().equalsIgnoreCase(string);
	}

	default boolean is(Signal signal) {
		return this.equals(signal);
	}

	String name();

}
