package org.nextrtc.signalingserver.modules;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import org.nextrtc.signalingserver.repository.ConversationRepository;
import org.nextrtc.signalingserver.repository.Conversations;
import org.nextrtc.signalingserver.repository.MemberRepository;
import org.nextrtc.signalingserver.repository.Members;

import javax.inject.Singleton;

@Module
public abstract class NextRTCRepositories {

    @Provides
    @Singleton
    static Members Members() {
        return new Members();
    }

    @Provides
    @Singleton
    static Conversations Conversations() {
        return new Conversations();
    }

    @Binds
    abstract ConversationRepository conversationRepository(Conversations conversations);

    @Binds
    abstract MemberRepository memberRepository(Members members);

}
