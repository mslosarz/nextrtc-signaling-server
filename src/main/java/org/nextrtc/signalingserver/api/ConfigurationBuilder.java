package org.nextrtc.signalingserver.api;

import org.nextrtc.signalingserver.factory.ConversationFactory;
import org.nextrtc.signalingserver.factory.ManualConversationFactory;
import org.nextrtc.signalingserver.repository.ConversationRepository;
import org.nextrtc.signalingserver.repository.Conversations;
import org.nextrtc.signalingserver.repository.MemberRepository;
import org.nextrtc.signalingserver.repository.Members;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ConfigurationBuilder {
    private ScheduledExecutorService service;
    private Map<Class, Object> maps = new ConcurrentHashMap<>();

    //TODO: use dagger or something
    public ConfigurationBuilder createDefaultEndpoint() {
        service = new ScheduledThreadPoolExecutor(3);
        maps.put(NextRTCEventBus.class, new NextRTCEventBus());

        maps.put(MemberRepository.class, new Members());
        maps.put(ConversationRepository.class, new Conversations());

        maps.put(ConversationFactory.class, new ManualConversationFactory());


        return this;
    }

    public ConfigurationBuilder withThreadPool(ScheduledExecutorService service) {
        this.service = service;
        return this;
    }

    public ConfigurationBuilder withEventBus(NextRTCEventBus eventBus) {
        maps.put(NextRTCEventBus.class, eventBus);
        return this;
    }

    public NextRTCEndpoint build() {
        return new NextRTCEndpoint();
    }
}
