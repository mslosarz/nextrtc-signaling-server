package org.nextrtc.signalingserver.codec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.nextrtc.signalingserver.domain.Message;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

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
        return gson.toJson(message);
    }
}
