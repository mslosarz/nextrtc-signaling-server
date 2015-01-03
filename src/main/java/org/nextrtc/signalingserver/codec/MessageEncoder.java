package org.nextrtc.signalingserver.codec;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import lombok.extern.log4j.Log4j;

import org.nextrtc.signalingserver.domain.Message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Log4j
public class MessageEncoder implements Encoder.Text<Message> {

	private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

	@Override
	public void destroy() {
	}

	@Override
	public void init(EndpointConfig config) {
	}

	@Override
	public String encode(Message message) throws EncodeException {
		String json = gson.toJson(message);
		log.info(String.format("Response: %s", json));
		return json;
	}
}
