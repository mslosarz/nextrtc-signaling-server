package org.nextrtc.signalingserver.domain;

import java.util.Map;

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

	public void execute() {
		signal.executeMessage(this);
	}

}
