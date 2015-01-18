package org.nextrtc.signalingserver.domain;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.Map;

import lombok.Getter;

import com.google.common.collect.Maps;
import com.google.gson.annotations.Expose;

@Getter
public class Message {
	/**
	 * Use Message.create(...) instead of new Message()
	 */
	@Deprecated
	public Message() {
	}

	@Expose
	private String from = EMPTY;

	@Expose
	private String to = EMPTY;

	@Expose
	private String signal = EMPTY;

	@Expose
	private String content = EMPTY;

	@Expose
	private Map<String, String> parameters = Maps.newHashMap();

	public static MessageBuilder create() {
		return new MessageBuilder();
	}

	public static class MessageBuilder {
		private Message instance = new Message();

		public MessageBuilder from(String from) {
			instance.from = from;
			return this;
		}

		public MessageBuilder to(String to) {
			instance.to = to;
			return this;
		}

		public MessageBuilder signal(String signal) {
			instance.signal = signal;
			return this;
		}

		public MessageBuilder content(String content) {
			instance.content = content;
			return this;
		}

		public MessageBuilder parameter(String key, String value) {
			instance.parameters.put(key, value);
			return this;
		}

		public MessageBuilder parameters(Map<String, String> map) {
			instance.parameters.putAll(map);
			return this;
		}

		public Message build() {
			return instance;
		}
	}
}
