package org.nextrtc.signalingserver.domain;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import lombok.Getter;

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

	@Override
	public String toString() {
		return String.format("(%s -> %s)[%s]: %s", from, to, signal, content);
	}

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

		public Message build() {
			return instance;
		}
	}
}
