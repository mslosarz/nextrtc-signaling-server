package org.nextrtc.signalingserver.domain;

import org.junit.Before;
import org.junit.Test;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.MessageMatcher;
import org.nextrtc.signalingserver.api.dto.NextRTCEvent;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ServerEventCheck.class, LocalStreamCreated2.class})
public class ServerTest extends BaseTest {

    @Autowired
    private Server server;

    @Autowired
    private Members members;

    @Autowired
    protected ServerEventCheck eventCheckerCall;

    @Autowired
    protected LocalStreamCreated2 eventLocalStream;

    @Test
    public void shouldSendExceptionMessageWhenMemberDoesntExists() throws Exception {
        // given
        MessageMatcher match = new MessageMatcher();
        Connection connection = mockConnection("s1", match);

        // when
        server.handle(mock(Message.class), connection);

        // then
        assertThat(match.getMessage().getTo(), is("s1"));
        assertThat(match.getMessage().getContent(), is("MEMBER_NOT_FOUND"));
        assertThat(match.getMessage().getSignal(), is("error"));
        assertThat(match.getMessage().getCustom().size(), is(1));
        assertTrue(match.getMessage().getCustom().containsKey("stackTrace"));
    }

    @Test
    public void shouldRegisterUserOnWebSocketConnection() throws Exception {
        // given
        Connection connection = mock(Connection.class);
        when(connection.getId()).thenReturn("s1");

        // when
        server.register(connection);

        // then
        assertTrue(members.findBy("s1").isPresent());

    }

    @Test
    public void shouldCreateConversationOnCreateSignal() throws Exception {
        // given
        Connection connection = mockConnection("s1");
        server.register(connection);

        // when
        server.handle(Message.create()//
                .signal("create")//
                .build(), connection);

        // then
        List<NextRTCEvent> events = eventCheckerCall.getEvents();
        assertThat(events.size(), is(2));
    }

    @Test
    public void shouldCreateConversationOnJoinSignal_WhenConversationDoesntExists() throws Exception {
        // given
        MessageMatcher s1Matcher = new MessageMatcher();
        Connection connection = mockConnection("s1", s1Matcher);
        server.register(connection);

        // when
        server.handle(Message.create()//
                .signal("join")//
                .build(), connection);

        // then
        List<NextRTCEvent> events = eventCheckerCall.getEvents();
        assertThat(events.size(), is(2));
        assertThat(s1Matcher.getMessage().getTo(), is("s1"));
        assertThat(s1Matcher.getMessage().getSignal(), is("created"));
    }

    @Test
    public void shouldCreateConversation() throws Exception {
        // given
        MessageMatcher s1Matcher = new MessageMatcher();
        MessageMatcher s2Matcher = new MessageMatcher();
        Connection s1 = mockConnection("s1", s1Matcher);
        Connection s2 = mockConnection("s2", s2Matcher);
        server.register(s1);
        server.register(s2);

        // when
        server.handle(Message.create()//
                .signal("create")//
                .build(), s1);

        // then
        assertThat(s1Matcher.getMessages().size(), is(1));
        assertThat(s1Matcher.getMessage().getSignal(), is("created"));
        assertThat(s2Matcher.getMessages().size(), is(0));
    }

    @Test
    public void shouldCreateConversationThenJoinAndSendOfferRequest() throws Exception {
        // given
        MessageMatcher s1Matcher = new MessageMatcher();
        MessageMatcher s2Matcher = new MessageMatcher();
        Connection s1 = mockConnection("s1", s1Matcher);
        Connection s2 = mockConnection("s2", s2Matcher);
        server.register(s1);
        server.register(s2);

        // when
        server.handle(Message.create()//
                .signal("create")//
                .build(), s1);
        String conversationKey = s1Matcher.getMessage().getContent();
        // s1Matcher.reset();
        server.handle(Message.create()//
                .signal("join")//
                .content(conversationKey)//
                .build(), s2);
        await().until(() -> s1Matcher.has(m -> m.getSignal()
                .equals("created"))
                .check());
        await().until(() -> s1Matcher.has(m -> m.getSignal()
                .equals("offerRequest"))
                .check());
        await().until(() -> s2Matcher.has(m -> m.getSignal()
                .equals("joined"))
                .check());
        // then
        assertThat(s1Matcher.getMessages().size(), is(3));
        assertMessage(s1Matcher, 0, EMPTY, "s1", "created", conversationKey);
        assertMessage(s1Matcher, 1, "s2", "s1", "newJoined", "s2");
        assertMessage(s1Matcher, 2, "s2", "s1", "offerRequest", EMPTY);

        assertThat(s2Matcher.getMessages().size(), is(2));
        assertMessage(s2Matcher, 0, EMPTY, "s2", "joined", conversationKey);
        assertMessage(s2Matcher, 1, "s1", "s2", "newJoined", "s1");
    }

    @Test
    public void shouldCreateConversationJoinMemberAndPassOfferResponseToRestMembers() throws Exception {
        // given
        MessageMatcher s1Matcher = new MessageMatcher();
        MessageMatcher s2Matcher = new MessageMatcher();
        Connection s1 = mockConnection("s1", s1Matcher);
        Connection s2 = mockConnection("s2", s2Matcher);
        server.register(s1);
        server.register(s2);

        server.handle(Message.create()//
                .signal("create")//
                .build(), s1);
        await().until(() -> s1Matcher.has(m -> m.getSignal()
                .equals("created"))
                .check());
        String conversationKey = s1Matcher.getMessage().getContent();
        server.handle(Message.create()//
                .signal("join")//
                .content(conversationKey)//
                .build(), s2);
        await().until(() -> s2Matcher.has(m -> m.getSignal()
                .equals("joined"))
                .check());
        s1Matcher.reset();
        s2Matcher.reset();

        // when
        // s2 has to create local stream
        server.handle(Message.create()//
                .to("s2")//
                .signal("offerResponse")//
                .content("s2 spd")//
                .build(), s1);
        await().until(() -> s2Matcher.has(m -> m.getSignal()
                .equals("answerRequest"))
                .check());

        // then
        assertThat(s2Matcher.getMessages().size(), is(1));
        assertMessage(s2Matcher, 0, "s1", "s2", "answerRequest", "s2 spd");

        assertThat(s1Matcher.getMessages().size(), is(0));
    }

    @Test
    public void shouldCreateConversationJoinMemberAndPassOfferResponseToRestTwoMembers() throws Exception {
        // given
        MessageMatcher s1Matcher = new MessageMatcher();
        MessageMatcher s2Matcher = new MessageMatcher();
        MessageMatcher s3Matcher = new MessageMatcher();
        Connection s1 = mockConnection("s1", s1Matcher);
        Connection s2 = mockConnection("s2", s2Matcher);
        Connection s3 = mockConnection("s3", s3Matcher);
        server.register(s1);
        server.register(s2);
        server.register(s3);

        server.handle(Message.create()//
                .signal("create")//
                .build(), s1);
        String conversationKey = s1Matcher.getMessage().getContent();
        await().until(() -> s1Matcher.has(m -> m.getSignal()
                .equals("created"))
                .check());
        server.handle(Message.create()//
                .signal("join")//
                .content(conversationKey)//
                .build(), s2);
        await().until(() -> s2Matcher.has(m -> m.getSignal()
                .equals("joined"))
                .check());
        server.handle(Message.create()//
                .signal("join")//
                .content(conversationKey)//
                .build(), s3);
        await().until(() -> s3Matcher.has(m -> m.getSignal()
                .equals("joined"))
                .check());
        s1Matcher.reset();
        s2Matcher.reset();
        s3Matcher.reset();
        // when
        // s2 has to create local stream
        server.handle(Message.create()//
                .to("s1")//
                .signal("offerResponse")//
                .content("s2 spd")//
                .build(), s2);
        await().until(() -> s2Matcher.has(m -> m.getSignal()
                .equals("answerRequest"))
                .check());
        // s3 has to create local stream
        server.handle(Message.create()//
                .to("s1")//
                .signal("offerResponse")//
                .content("s3 spd")//
                .build(), s3);
        await().until(() -> s3Matcher.has(m -> m.getSignal()
                .equals("answerRequest"))
                .check());

        // then
        assertThat(s2Matcher.getMessages().size(), is(1));
        assertMessage(s2Matcher, 0, "s1", "s2", "answerRequest", "s2 spd");
        assertThat(s3Matcher.getMessages().size(), is(1));
        assertMessage(s3Matcher, 0, "s1", "s3", "answerRequest", "s3 spd");
        assertThat(s1Matcher.getMessages().size(), is(0));
    }

    @Test
    public void shouldExchangeSpds() throws Exception {
        // given
        MessageMatcher s1Matcher = new MessageMatcher();
        MessageMatcher s2Matcher = new MessageMatcher();
        Connection s1 = mockConnection("s1", s1Matcher);
        Connection s2 = mockConnection("s2", s2Matcher);
        server.register(s1);
        server.register(s2);

        server.handle(Message.create()//
                .signal("create")//
                .build(), s1);
        // -> created
        String conversationKey = s1Matcher.getMessage().getContent();
        server.handle(Message.create()//
                .signal("join")//
                .content(conversationKey)//
                .build(), s2);
        // -> joined
        // -> offerRequest
        await().until(() -> s1Matcher.has(m -> m.getSignal()
                .equals("offerRequest"))
                .check());

        server.handle(Message.create()//
                .to("s1")//
                .signal("offerResponse")//
                .content("s2 spd")//
                .build(), s2);

        // -> answerRequest
        await().until(() -> s2Matcher.has(m -> m.getSignal()
                .equals("answerRequest"))
                .check());

        s1Matcher.reset();
        s2Matcher.reset();
        // when
        server.handle(Message.create()//
                .to("s2")//
                .signal("answerResponse")//
                .content("s1 spd")//
                .build(), s1);

        await().until(() -> s1Matcher
                .has(s -> s.getSignal().equals("finalize"))
                .check());

        // then
        assertThat(s1Matcher.getMessages().size(), is(1));
        assertMessage(s1Matcher, 0, "s2", "s1", "finalize", "s1 spd");
        assertThat(s2Matcher.getMessages().size(), is(0));
    }

    @Test
    public void shouldExchangeCandidates() throws Exception {
        // given
        MessageMatcher s1Matcher = new MessageMatcher();
        MessageMatcher s2Matcher = new MessageMatcher();
        Connection s1 = mockConnection("s1", s1Matcher);
        Connection s2 = mockConnection("s2", s2Matcher);
        server.register(s1);
        server.register(s2);

        server.handle(Message.create()//
                .signal("create")//
                .build(), s1);
        // -> created
        await().until(() -> s1Matcher.has(m -> m.getSignal()
                .equals("created"))
                .check());

        String conversationKey = s1Matcher.getMessage().getContent();
        server.handle(Message.create()//
                .signal("join")//
                .content(conversationKey)//
                .build(), s2);
        // -> joined
        await().until(() -> s2Matcher.has(m -> m.getSignal()
                .equals("joined"))
                .check());
        // -> offerRequest
        await().until(() -> s1Matcher.has(m -> m.getSignal()
                .equals("offerRequest"))
                .check());
        server.handle(Message.create()//
                .to("s1")//
                .signal("offerResponse")//
                .content("s2 spd")//
                .build(), s2);
        // -> answerRequest
        await().until(() -> s2Matcher.has(m -> m.getSignal()
                .equals("answerRequest"))
                .check());
        server.handle(Message.create()//
                .to("s2")//
                .signal("answerResponse")//
                .content("s1 spd")//
                .build(), s1);
        // -> finalize
        await().until(() -> s1Matcher.has(m -> m.getSignal()
                .equals("finalize"))
                .check());
        s1Matcher.reset();
        s2Matcher.reset();
        server.handle(Message.create()//
                .to("s1")//
                .signal("candidate")//
                .content("candidate s2")//
                .build(), s2);
        server.handle(Message.create()//
                .to("s2")//
                .signal("candidate")//
                .content("candidate s1")//
                .build(), s1);
        await().until(() -> s1Matcher.has(m -> m.getSignal()
                .equals("candidate"))
                .check());
        await().until(() -> s2Matcher.has(m -> m.getSignal()
                .equals("candidate"))
                .check());
        // when

        // then
        assertThat(s1Matcher.getMessages().size(), is(1));
        assertMessage(s1Matcher, 0, "s2", "s1", "candidate", "candidate s2");

        assertThat(s2Matcher.getMessages().size(), is(1));
        assertMessage(s2Matcher, 0, "s1", "s2", "candidate", "candidate s1");
    }

    @Test
    public void shouldUnregisterSession() throws Exception {
        // given
        MessageMatcher s1Matcher = new MessageMatcher();
        MessageMatcher s2Matcher = new MessageMatcher();
        Connection s1 = mockConnection("s1", s1Matcher);
        Connection s2 = mockConnection("s2", s2Matcher);
        server.register(s1);
        server.register(s2);

        // when
        server.handle(Message.create()//
                .signal("create")//
                .build(), s1);
        // -> created
        await().until(() -> s1Matcher.has(m -> m.getSignal().equals("created")).check());

        String conversationKey = s1Matcher.getMessage().getContent();
        server.handle(Message.create()//
                .signal("join")//
                .content(conversationKey)//
                .build(), s2);
        await().until(() -> s2Matcher.has(m -> m.getSignal().equals("joined")).check());
        s1Matcher.reset();
        s2Matcher.reset();
        server.unregister(s1, "");
        await().until(() -> s2Matcher.has(m -> m.getSignal().equals("left")).check());
        // then
        assertThat(s1Matcher.getMessages().size(), is(0));
        assertThat(s2Matcher.getMessages().size(), is(1));
        assertMessage(s2Matcher, 0, "s1", "s2", "left", EMPTY);
    }

    @Before
    public void resetObjects() {
        eventCheckerCall.reset();
        eventLocalStream.reset();
    }
}
