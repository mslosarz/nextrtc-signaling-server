package org.nextrtc.signalingserver;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.cases.SignalHandler;
import org.nextrtc.signalingserver.domain.SignalResolver;
import org.nextrtc.signalingserver.domain.resolver.ManualSignalResolver;
import org.nextrtc.signalingserver.eventbus.ManualEventDispatcher;
import org.nextrtc.signalingserver.property.ManualNextRTCProperties;
import org.nextrtc.signalingserver.property.NextRTCProperties;

import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Module
public abstract class NextRTCBeans {

    @Provides
    @Singleton
    static NextRTCEventBus NextRTCEventBus() {
        return new NextRTCEventBus();
    }

    @Provides
    @Singleton
    static ScheduledExecutorService ScheduledExecutorService(NextRTCProperties properties) {
        return Executors.newScheduledThreadPool(properties.getMaxConnectionSetupTime());
    }

    @Provides
    @Singleton
    static ManualNextRTCProperties ManualNextRTCProperties() {
        return new ManualNextRTCProperties();
    }

    @Binds
    abstract NextRTCProperties NextRTCProperties(ManualNextRTCProperties properties);

    @Provides
    @Singleton
    static SignalResolver ManualSignalResolver(Map<String, SignalHandler> signals) {
        return new ManualSignalResolver(signals);
    }

    @Provides
    @Singleton
    static ManualEventDispatcher ManualEventDispatcher() {
        return new ManualEventDispatcher();
    }
}
