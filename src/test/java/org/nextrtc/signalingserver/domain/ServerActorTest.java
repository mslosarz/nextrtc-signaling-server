package org.nextrtc.signalingserver.domain;

import org.junit.Before;
import org.junit.Test;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.repository.Conversations;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@ContextConfiguration(classes = {ServerEventCheck.class, LocalStreamCreated2.class})
public class ServerActorTest extends BaseTest {

    @Autowired
    private Server server;

    @Autowired
    private Members members;

    @Autowired
    private Conversations conversations;

    @Autowired
    protected ServerEventCheck eventCheckerCall;

    @Autowired
    protected LocalStreamCreated2 eventLocalStream;

    @Test
    public void shouldExchangeSignalsBetweenActors() throws Exception {
        // given
        TestClientActor john = new TestClientActor("John", server);
        TestClientActor bob = new TestClientActor("Bob", server);

        // when
        john.openSocket();
        john.create("AAA", "MESH");

        // then
        assertTrue(conversations.findBy("AAA").isPresent());
        Conversation conversation = conversations.findBy("AAA").get();
        assertTrue(conversation.has(john.asMember()));

        // when
        bob.openSocket();
        bob.join("AAA");

        // then
        assertTrue(conversation.has(john.asMember()));
        assertTrue(conversation.has(bob.asMember()));

        // when
        bob.closeSocket();

        // then
        assertFalse(conversation.has(bob.asMember()));

        // when
        john.closeSocket();

        // then
        assertFalse(conversation.has(john.asMember()));
        assertFalse(conversations.findBy("AAA").isPresent());

        assertNoErrors(john);
        assertNoErrors(bob);
    }

    @Test
    public void shouldExchangeSignalsBetweenActors_Broadcast() throws Exception {
        // given
        TestClientActor john = new TestClientActor("John", server);
        TestClientActor bob = new TestClientActor("Bob", server);

        // when
        john.openSocket();
        john.create("AAA", "BROADCAST");

        // then
        assertTrue(conversations.findBy("AAA").isPresent());
        Conversation conversation = conversations.findBy("AAA").get();
        assertTrue(conversation.has(john.asMember()));

        // when
        bob.openSocket();
        bob.join("AAA");

        // then
        assertTrue(conversation.has(john.asMember()));
        assertTrue(conversation.has(bob.asMember()));

        // when
        bob.closeSocket();

        // then
        assertFalse(conversation.has(bob.asMember()));

        // when
        john.closeSocket();

        // then
        assertFalse(conversation.has(john.asMember()));
        assertFalse(conversations.findBy("AAA").isPresent());

        assertNoErrors(john);
        assertNoErrors(bob);
    }


    @Test
    public void shouldCheckBehaviorWhenBroadcasterWillEndConnectionFirst() throws Exception {
        // given
        TestClientActor john = new TestClientActor("John", server);
        TestClientActor bob = new TestClientActor("Bob", server);

        // when
        john.openSocket();
        john.create("AAA", "BROADCAST");
        Conversation conversation = conversations.findBy("AAA").get();

        bob.openSocket();
        bob.join("AAA");

        john.closeSocket();
        bob.closeSocket();


        // then
        assertFalse(conversation.has(bob.asMember()));
        assertFalse(conversations.findBy("AAA").isPresent());
        final Message message = bob.getMessages().stream().filter(m -> m.getSignal().equals(Signals.END)).findFirst().get();
        assertThat(message.getContent(), is("AAA"));
        assertThat(message.getSignal(), is(Signals.END));

        assertNoErrors(john);
        assertNoErrors(bob);
    }

    @Test
    public void shouldCheckSignalExchangeForThreeMembers() throws Exception {
        // given
        TestClientActor john = new TestClientActor("John", server);
        TestClientActor bob = new TestClientActor("Bob", server);
        TestClientActor alice = new TestClientActor("Alice", server);

        // when
        alice.openSocket();

        john.openSocket();
        john.create("AAA", "BROADCAST");
        Conversation conversation = conversations.findBy("AAA").get();

        bob.openSocket();
        bob.join("AAA");
        alice.join("AAA");

        john.closeSocket();
        bob.closeSocket();
        alice.closeSocket();

        // then
        assertThat(bob.getMessages().size(), is(alice.getMessages().size()));
        List<Message> bobMessages = bob.getMessages();
        List<Message> aliceMessages = alice.getMessages();
        for (int i = 0; i < bobMessages.size(); i++) {
            Message bobMsg = bobMessages.get(i);
            Message aliceMsg = aliceMessages.get(i);
            assertTrue(bobMsg.getSignal().equals(aliceMsg.getSignal()));
        }

        assertNoErrors(john);
        assertNoErrors(bob);
        assertNoErrors(alice);
    }


    private void assertNoErrors(TestClientActor john) {
        assertTrue(john.getMessages().stream().allMatch(m -> !m.getSignal().equals(Signals.ERROR)));
    }


    @Before
    public void resetObjects() {
        eventCheckerCall.reset();
        eventLocalStream.reset();
    }
}
