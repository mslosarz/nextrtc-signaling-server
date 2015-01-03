package org.nextrtc.signalingserver.domain;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.Map;

import lombok.Getter;

import com.google.common.collect.Maps;
import com.google.gson.annotations.Expose;

@Getter
public class Message {

	/**
	 * Use Message.createWith(...) instead of new Message()
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

}
