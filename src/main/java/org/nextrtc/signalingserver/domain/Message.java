package org.nextrtc.signalingserver.domain;

import com.google.common.collect.Maps;
import com.google.gson.annotations.Expose;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Getter
@Builder(builderMethodName = "create")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
}
