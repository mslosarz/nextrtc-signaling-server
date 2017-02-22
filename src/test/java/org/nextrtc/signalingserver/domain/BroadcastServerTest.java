package org.nextrtc.signalingserver.domain;

import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.MessageMatcher;
import org.nextrtc.signalingserver.api.dto.NextRTCEvent;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@ContextConfiguration(classes = {ServerEventCheck.class, LocalStreamCreated2.class})
public class BroadcastServerTest extends BaseTest {

    @Rule
    public ExpectedException expect = ExpectedException.none();

    @Autowired
    private Server server;

    @Autowired
    private Members members;

    @Autowired
    protected ServerEventCheck eventCheckerCall;

    @Autowired
    protected LocalStreamCreated2 eventLocalStream;

    @Test
    public void shouldCreateConversationOnCreateSignal() throws Exception {
        // given
        Session session = mockSession("s1");
        server.register(session);

        // when
        server.handle(Message.create()//
                .signal("create")//
                .custom(broadcast())
                .build(), session);

        // then
        List<NextRTCEvent> events = eventCheckerCall.getEvents();
        assertThat(events.size(), is(2));
    }

    @Test
    public void shouldCreateConversation() throws Exception {
        // given
        MessageMatcher s1Matcher = new MessageMatcher();
        MessageMatcher s2Matcher = new MessageMatcher();
        Session s1 = mockSession("s1", s1Matcher);
        Session s2 = mockSession("s2", s2Matcher);
        server.register(s1);
        server.register(s2);

        // when
        server.handle(Message.create()//
                .signal("create")//
                .custom(broadcast())
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
        Session s1 = mockSession("s1", s1Matcher);
        Session s2 = mockSession("s2", s2Matcher);
        server.register(s1);
        server.register(s2);

        // when
        server.handle(Message.create()//
                .signal("create")//
                .custom(broadcast())
                .build(), s1);
        String conversationKey = s1Matcher.getMessage().getContent();
        // s1Matcher.reset();
        server.handle(Message.create()//
                .signal("join")//
                .content(conversationKey)//
                .build(), s2);

        // then
        assertThat(s1Matcher.getMessages().size(), is(3));
        assertMessage(s1Matcher, 0, EMPTY, "s1", "created", conversationKey);
        assertMessage(s1Matcher, 1, "s2", "s1", "newJoined", "s2");
        assertMessage(s1Matcher, 2, "s2", "s1", "offerRequest", EMPTY);

        assertThat(s2Matcher.getMessages().size(), is(1));
        assertMessage(s2Matcher, 0, EMPTY, "s2", "joined", conversationKey);
    }

    @Test
    public void shouldCreateConversationJoinMemberAndPassOfferResponseToRestMembers() throws Exception {
        // given
        MessageMatcher s1Matcher = new MessageMatcher();
        MessageMatcher s2Matcher = new MessageMatcher();
        Session s1 = mockSession("s1", s1Matcher);
        Session s2 = mockSession("s2", s2Matcher);
        server.register(s1);
        server.register(s2);

        server.handle(Message.create()//
                .custom(broadcast())
                .signal("create")//
                .build(), s1);
        String conversationKey = s1Matcher.getMessage().getContent();
        server.handle(Message.create()//
                .signal("join")//
                .content(conversationKey)//
                .build(), s2);
        s1Matcher.reset();
        s2Matcher.reset();

        // when
        // s2 has to create local stream
        server.handle(Message.create()//
                .to("s2")//
                .signal("offerResponse")//
                .content("s2 spd")//
                .build(), s1);

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
        Session s1 = mockSession("s1", s1Matcher);
        Session s2 = mockSession("s2", s2Matcher);
        Session s3 = mockSession("s3", s3Matcher);
        server.register(s1);
        server.register(s2);
        server.register(s3);

        server.handle(Message.create()//
                .signal("create")//
                .custom(broadcast())
                .build(), s1);
        String conversationKey = s1Matcher.getMessage().getContent();
        server.handle(Message.create()//
                .signal("join")//
                .content(conversationKey)//
                .build(), s2);
        server.handle(Message.create()//
                .signal("join")//
                .content(conversationKey)//
                .build(), s3);
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
        // s3 has to create local stream
        server.handle(Message.create()//
                .to("s1")//
                .signal("offerResponse")//
                .content("s3 spd")//
                .build(), s3);

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
        Session s1 = mockSession("s1", s1Matcher);
        Session s2 = mockSession("s2", s2Matcher);
        server.register(s1);
        server.register(s2);

        server.handle(Message.create()//
                .signal("create")//
                .custom(broadcast())
                .build(), s1);
        // -> created
        String conversationKey = s1Matcher.getMessage().getContent();
        server.handle(Message.create()//
                .signal("join")//
                .content(conversationKey)//
                .build(), s2);
        // -> joined
        // -> offerRequest
        server.handle(Message.create()//
                .to("s1")//
                .signal("offerResponse")//
                .content("s2 spd")//
                .build(), s2);
        // -> answerRequest
        s1Matcher.reset();
        s2Matcher.reset();
        // when
        server.handle(Message.create()//
                .to("s2")//
                .signal("answerResponse")//
                .content("s1 spd")//
                .build(), s1);

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
        Session broadcaster = mockSession("broadcaster", s1Matcher);
        Session s2 = mockSession("s2", s2Matcher);
        server.register(broadcaster);
        server.register(s2);

        server.handle(Message.create()//
                .signal("create")//
                .custom(broadcast())
                .build(), broadcaster);
        // -> created
        String conversationKey = s1Matcher.getMessage().getContent();
        server.handle(Message.create()//
                .signal("join")//
                .content(conversationKey)//
                .build(), s2);
        // -> joined
        // -> offerRequest
        server.handle(Message.create()//
                .to("broadcaster")//
                .signal("offerResponse")//
                .content("s2 spd")//
                .build(), s2);
        // -> answerRequest
        server.handle(Message.create()//
                .to("s2")//
                .signal("answerResponse")//
                .content("broadcaster spd")//
                .build(), broadcaster);
        // -> finalize
        s1Matcher.reset();
        s2Matcher.reset();
        server.handle(Message.create()//
                .to("broadcaster")//
                .signal("candidate")//
                .content("candidate s2")//
                .build(), s2);
        server.handle(Message.create()//
                .to("s2")//
                .signal("candidate")//
                .content("candidate broadcaster")//
                .build(), broadcaster);
        // when

        // then
        assertThat(s1Matcher.getMessages().size(), is(1));
        assertThat(s2Matcher.getMessages().size(), is(1));
        assertMessage(s2Matcher, 0, "broadcaster", "s2", "candidate", "candidate broadcaster");
        assertMessage(s1Matcher, 0, "s2", "broadcaster", "candidate", "candidate s2");
    }

    @Test
    public void shouldUnregisterSession() throws Exception {
        // given
        MessageMatcher s1Matcher = new MessageMatcher();
        MessageMatcher s2Matcher = new MessageMatcher();
        Session s1 = mockSession("s1", s1Matcher);
        Session s2 = mockSession("s2", s2Matcher);
        server.register(s1);
        server.register(s2);

        server.handle(Message.create()//
                .custom(broadcast())
                .signal("create")//
                .build(), s1);
        // -> created
        String conversationKey = s1Matcher.getMessage().getContent();
        server.handle(Message.create()//
                .signal("join")//
                .content(conversationKey)//
                .build(), s2);
        s1Matcher.reset();
        s2Matcher.reset();
        server.unregister(s1, mock(CloseReason.class));
        // when

        // then
        assertThat(s1Matcher.getMessages().size(), is(0));
        assertThat(s2Matcher.getMessages().size(), is(2));
        assertMessage(s2Matcher, 0, "s1", "s2", "left", EMPTY);
    }

    @Test
    public void shouldInformAudienceAboutMissingBroadcaster() throws Exception {
        // given
        MessageMatcher s1Matcher = new MessageMatcher();
        MessageMatcher s2Matcher = new MessageMatcher();
        Session s1 = mockSession("s1", s1Matcher);
        Session s2 = mockSession("s2", s2Matcher);
        server.register(s1);
        server.register(s2);

        server.handle(Message.create()//
                .custom(broadcast())
                .signal("create")//
                .build(), s1);
        // -> created
        String conversationKey = s1Matcher.getMessage().getContent();
        server.handle(Message.create()//
                .signal("join")//
                .content(conversationKey)//
                .build(), s2);
        s1Matcher.reset();
        s2Matcher.reset();
        server.unregister(s1, mock(CloseReason.class));
        // when

        // then
        assertThat(s1Matcher.getMessages().size(), is(0));
        assertThat(s2Matcher.getMessages().size(), is(2));
        assertMessage(s2Matcher, 0, "s1", "s2", "left", EMPTY);
        assertMessage(s2Matcher, 1, "s1", "s2", "end", conversationKey);
    }

    private HashMap<String, String> broadcast() {
        HashMap<String, String> custom = Maps.newHashMap();
        custom.put("type", "BROADCAST");
        return custom;
    }

    @Before
    public void resetObjects() {
        eventCheckerCall.reset();
        eventLocalStream.reset();
        members.unregisterBy(mockSession("s1"), null);
        members.unregisterBy(mockSession("s2"), null);
        members.unregisterBy(mockSession("s3"), null);
    }
}
