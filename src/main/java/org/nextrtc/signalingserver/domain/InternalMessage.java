package org.nextrtc.signalingserver.domain;

import java.util.Map;

import lombok.Getter;

import org.nextrtc.signalingserver.domain.signal.Signal;

import com.google.common.collect.Maps;

@Getter
public class InternalMessage {

	private Member from;
	private Member to;
	private Signal signal;
	private String content;
	private Map<String, String> parameters = Maps.newHashMap();

	public static InternalMessageBuilder create() {
		return new InternalMessageBuilder();
	}

	public static class InternalMessageBuilder {
		private InternalMessage instance = new InternalMessage();

		public InternalMessageBuilder from(Member from) {
			instance.from = from;
			return this;
		}

		public InternalMessageBuilder to(Member to) {
			instance.to = to;
			return this;
		}

		public InternalMessageBuilder withSignal(Signal signal) {
			instance.signal = signal;
			return this;
		}

		public InternalMessageBuilder withContent(String content) {
			instance.content = content;
			return this;
		}

		public InternalMessageBuilder withParams(Map<String, String> params) {
			instance.parameters.putAll(params);
			return this;
		}

		public InternalMessage build() {
			return instance;
		}
	}

}
