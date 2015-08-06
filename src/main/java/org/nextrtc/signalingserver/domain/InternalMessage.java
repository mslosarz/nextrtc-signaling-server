package org.nextrtc.signalingserver.domain;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;

import javax.websocket.RemoteEndpoint.Async;

import lombok.Getter;
import lombok.experimental.Builder;

@Getter
@Builder(builderMethodName = "create")
public class InternalMessage {

	private Member from;
	private Member to;
	private Signal signal;
	private String content;

	private InternalMessage(Member from, Member to, Signal signal, String content) {
		this.from = from;
		this.to = to;
		this.signal = signal;
		this.content = content;
	}

	/**
	 * Method will post message to recipient (member To)
	 */
	public void post() {
		getRemotePeer().sendObject(transformToExternalMessage());
	}

	private Message transformToExternalMessage() {
		return Message.create()//
				.from(fromNullable(from))//
				.to(fromNullable(to))//
				.signal(signal.name())//
				.content(defaultString(content))//
				.build();
	}

	private String fromNullable(Member member) {
		return member == null ? EMPTY : member.getId();
	}

	private Async getRemotePeer() {
		return to.getSession().getAsyncRemote();
	}

	public boolean isCreate() {
		return Signal.CREATE.is(signal);
	}

	public boolean isJoin() {
		return Signal.JOIN.is(signal);
	}

	@Override
	public String toString() {
		return String.format("(%s -> %s)[%s]: %s", from, to, signal != null ? signal.name() : null, content);
	}

	public boolean isLeft() {
		return Signal.LEFT.is(signal);
	}
}
