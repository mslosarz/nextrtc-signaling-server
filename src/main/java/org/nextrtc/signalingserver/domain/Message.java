package org.nextrtc.signalingserver.domain;

import com.google.common.collect.Maps;
import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.util.Map;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Getter
public class Message {
    /**
     * Use Message.create(...) instead of new Message()
     */
    @Deprecated
    Message() {
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
    private Map<String, String> custom = Maps.newHashMap();

    @Override
    public String toString() {
        return String.format("(%s -> %s)[%s]: %s |%s", from, to, signal, content, custom);
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

        public MessageBuilder custom(String key, String value) {
            instance.custom.put(key, value);
            return this;
        }

        public MessageBuilder custom(Map<String, String> custom) {
            instance.custom.putAll(custom);
            return this;
        }

        public Message build() {
            return instance;
        }

    }
}
