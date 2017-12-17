package org.nextrtc.signalingserver;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.nextrtc.signalingserver.cases.CreateConversation;
import org.nextrtc.signalingserver.cases.JoinConversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.Message;
import org.nextrtc.signalingserver.repository.Conversations;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.websocket.RemoteEndpoint;
import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {TestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class BaseTest {

    @Autowired
    private CreateConversation create;

    @Autowired
    private JoinConversation join;

    @Autowired
    private Members members;

    @Autowired
    private Conversations conversations;

    @Autowired
    private List<EventChecker> checkers;

    @Autowired
    private ApplicationContext context;

    @Before
    public void reset() {

        for (String id : conversations.getAllIds()) {
            conversations.remove(id);
        }
        for (String id : members.getAllIds()) {
            members.unregister(id);
        }
        for (EventChecker checker : checkers) {
            checker.reset();
        }
    }

    protected Session mockSession(String string) {
        return mockSession(string, new MessageMatcher());
    }

    protected Session mockSession(String id, ArgumentMatcher<Message> match) {
        Session s = mock(Session.class);
        when(s.getId()).thenReturn(id);
        Async mockAsync = mockAsync(match);
        RemoteEndpoint.Basic mockBasic = mockBasic(match);
        when(s.getAsyncRemote()).thenReturn(mockAsync);
        when(s.getBasicRemote()).thenReturn(mockBasic);
        return s;
    }

    protected Async mockAsync(ArgumentMatcher<Message> match) {
        Async async = mock(Async.class);
        when(async.sendObject(Mockito.argThat(match))).thenReturn(null);
        return async;
    }

    protected RemoteEndpoint.Basic mockBasic(ArgumentMatcher<Message> match) {
        try {
            RemoteEndpoint.Basic basic = mock(RemoteEndpoint.Basic.class);
            doNothing().when(basic).sendObject(Mockito.argThat(match));
            return basic;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Member mockMember(String string) {
        return context.getBean(Member.class, mockSession(string), mock(ScheduledFuture.class));
    }

    protected Member mockMember(String string, ArgumentMatcher<Message> match) {
        return context.getBean(Member.class, mockSession(string, match), mock(ScheduledFuture.class));
    }

    protected void createConversation(String conversationName, Member member) {
        create.execute(InternalMessage.create()//
                .from(member)//
                .content(conversationName)//
                .build());
    }

    protected void createBroadcastConversation(String conversationName, Member member) {
        create.execute(InternalMessage.create()//
                .from(member)//
                .content(conversationName)//
                .addCustom("type", "BROADCAST")//
                .build());
    }

    protected void joinConversation(String conversationName, Member member) {
        join.execute(InternalMessage.create()//
                .from(member)//
                .content(conversationName)//
                .build());
    }

    protected void assertMessage(MessageMatcher matcher, int number, String from, String to, String signal, String content) {
        assertThat(matcher.getMessage(number).getFrom(), is(from));
        assertThat(matcher.getMessage(number).getTo(), is(to));
        assertThat(matcher.getMessage(number).getSignal(), is(signal));
        assertThat(matcher.getMessage(number).getContent(), is(content));
    }
}
