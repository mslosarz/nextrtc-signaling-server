package org.nextrtc.signalingserver.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.nextrtc.signalingserver.domain.Connection;
import org.nextrtc.signalingserver.domain.Message;

import java.io.Closeable;
import java.util.function.Function;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

public interface NextRTCServer extends Closeable {

    void register(Connection connection);

    void handle(Message external, Connection connection);

    default void handle(String message, Connection connection) {
        Message decode;
        try {
            decode = MessageDecoder.decode(message);
        } catch (Exception e){
            throw new IllegalArgumentException(e);
        }
        handle(decode, connection);
    }

    void unregister(Connection connection, String reason);

    void handleError(Connection connection, Throwable exception);


    static NextRTCServer create(Function<EndpointConfiguration, EndpointConfiguration> function) {
        return function.apply(new ConfigurationBuilder().createDefaultEndpoint()).nextRTCServer();
    }

    class MessageDecoder {

        private static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        public static Message decode(String json) {
            return gson.fromJson(escapeHtml4(json.replace("\"", "'")), Message.class);
        }

    }

    class MessageEncoder {

        private static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        public static String encode(Object json) {
            return gson.toJson(json);
        }
    }
}
