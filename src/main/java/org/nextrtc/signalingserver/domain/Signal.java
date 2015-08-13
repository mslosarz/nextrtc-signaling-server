package org.nextrtc.signalingserver.domain;

import org.apache.commons.lang3.StringUtils;


public enum Signal {
	EMPTY {
		@Override
		public String ordinaryName() {
			return StringUtils.EMPTY;
		}
	},
	OFFER_REQUEST {
		@Override
		public String ordinaryName() {
			return "offerRequest";
		}
	},
	OFFER_RESPONSE {
		@Override
		public String ordinaryName() {
			return "offerResponse";
		}
	},
	ANSWER_REQUEST {
		@Override
		public String ordinaryName() {
			return "answerRequest";
		}
	},
	ANSWER_RESPONSE {
		@Override
		public String ordinaryName() {
			return "answerResponse";
		}
	},
	FINALIZE {
		@Override
		public String ordinaryName() {
			return "finalize";
		}
	},
	CANDIDATE {
		@Override
		public String ordinaryName() {
			return "candidate";
		}
	},
	PING {
		@Override
		public String ordinaryName() {
			return "ping";
		}
	},
	LEFT {
		@Override
		public String ordinaryName() {
			return "left";
		}
	},
	JOIN {
		@Override
		public String ordinaryName() {
			return "join";
		}
	},
	CREATE {
		@Override
		public String ordinaryName() {
			return "create";
		}
	},
	JOINED {
		@Override
		public String ordinaryName() {
			return "joined";
		}
	},
	CREATED {
		@Override
		public String ordinaryName() {
			return "created";
		}
	};

	public boolean is(String string) {
        return ordinaryName().equalsIgnoreCase(string);
	}

	public boolean is(Signal signal) {
		return this.equals(signal);
	}

	public abstract String ordinaryName();

}
