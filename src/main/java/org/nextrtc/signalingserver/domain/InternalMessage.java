package org.nextrtc.signalingserver.domain;

import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import javax.websocket.RemoteEndpoint.Async;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;

@Getter
@Log4j
@Builder(builderMethodName = "create")
public class InternalMessage {

	private Member from;
	private Member to;
	private Signal signal;
	private String content;
    private Map<String, String> custom = Maps.newHashMap();

    private InternalMessage(Member from, Member to, Signal signal, String content, Map<String, String> custom) {
		this.from = from;
		this.to = to;
		this.signal = signal;
		this.content = content;
        if (custom != null) {
            this.custom.putAll(custom);
        }
	}

	/**
	 * Method will send message to recipient (member To)
	 */
	public void send() {
		if (signal != Signal.PING) {
            log.info("Outgoing: " + toString());
        }
		getRemotePeer().sendObject(transformToExternalMessage());
	}

	private Message transformToExternalMessage() {
		return Message.create()//
				.from(fromNullable(from))//
				.to(fromNullable(to))//
                .signal(signal.ordinaryName())//
				.content(defaultString(content))//
                .custom(custom)//
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
        return String.format("(%s -> %s)[%s]: %s |%s", from, to, signal != null ? signal.ordinaryName() : null, content, custom);
	}

	public boolean isLeft() {
		return Signal.LEFT.is(signal);
	}
}
