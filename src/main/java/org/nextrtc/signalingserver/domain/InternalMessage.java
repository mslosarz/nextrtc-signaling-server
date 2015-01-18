package org.nextrtc.signalingserver.domain;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.Map;

import javax.websocket.RemoteEndpoint.Async;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Builder;

import org.nextrtc.signalingserver.domain.signal.Signal;

import com.google.common.collect.Maps;

@Getter
@ToString
@Builder(builderMethodName = "create")
public class InternalMessage {

	private Member from;
	private Member to;
	private Signal signal;
	private String content;
	private Map<String, String> parameters = Maps.newHashMap();

	public InternalMessage() {
	}

	public InternalMessage(Member from, Member to, Signal signal, String content, Map<String, String> parameters) {
		this.from = from;
		this.to = to;
		this.signal = signal;
		this.content = content;
		if (parameters != null) {
			this.parameters.putAll(parameters);
		}
	}

	public void execute() {
		signal.executeMessage(this);
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
				.content(content)//
				.parameters(parameters)//
				.build();
	}

	private String fromNullable(Member member) {
		return member == null ? EMPTY : member.getId();
	}

	private Async getRemotePeer() {
		return to.getSession().getAsyncRemote();
	}

}
