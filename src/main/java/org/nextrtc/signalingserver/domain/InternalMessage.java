package org.nextrtc.signalingserver.domain;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import javax.websocket.RemoteEndpoint.Basic;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;

@Log4j
@Getter
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

    public static InternalMessageBuilder create() {
        return new InternalMessageBuilder();
    }

    public InternalMessageBuilder copy() {
        return new InternalMessageBuilder().from(from).to(to).content(content).custom(custom).signal(signal);
    }

    /**
     * Method will send message to recipient (member To)
     */
    public synchronized void send() {
        if (signal != Signal.PING) {
            log.info("Outgoing: " + toString());
        }
        try {
            getRemotePeer().sendObject(transformToExternalMessage());
        } catch (Exception e) {
            log.debug("Unable to send message: " + transformToExternalMessage() + " error during sending!");
        }
    }

    synchronized void sendCarefully() {
        if (to.getSession().isOpen()) {
            send();
        } else {
            log.debug("Unable to send message: " + transformToExternalMessage() + " session is broken!");
        }
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

    private Basic getRemotePeer() {
        return to.getSession().getBasicRemote();
    }

    @Override
    public String toString() {
        return String.format("(%s -> %s)[%s]: %s |%s", from, to, signal != null ? signal.ordinaryName() : null, content, custom);
    }

    public static class InternalMessageBuilder {
        private Member from;
        private Member to;
        private Signal signal;
        private String content;
        private Map<String, String> custom = Maps.newHashMap();

        InternalMessageBuilder() {
        }

        public InternalMessage.InternalMessageBuilder from(Member from) {
            this.from = from;
            return this;
        }

        public InternalMessage.InternalMessageBuilder to(Member to) {
            this.to = to;
            return this;
        }

        public InternalMessage.InternalMessageBuilder signal(Signal signal) {
            this.signal = signal;
            return this;
        }

        public InternalMessage.InternalMessageBuilder content(String content) {
            this.content = content;
            return this;
        }

        public InternalMessage.InternalMessageBuilder custom(Map<String, String> custom) {
            if (custom != null) {
                this.custom.putAll(custom);
            }
            return this;
        }

        public InternalMessage.InternalMessageBuilder addCustom(String key, String value) {
            this.custom.put(key, value);
            return this;
        }

        public InternalMessage build() {
            return new InternalMessage(from, to, signal, content, custom);
        }

        public String toString() {
            return "InternalMessageBuilder(from=" + this.from + ", to=" + this.to + ", signal=" + this.signal + ", content=" + this.content + ", custom=" + this.custom + ")";
        }
    }
}
