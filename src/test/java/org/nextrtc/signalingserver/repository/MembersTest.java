package org.nextrtc.signalingserver.repository;

import org.junit.Test;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.domain.Connection;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MembersTest extends BaseTest {

    @Autowired
    private Members members;

    @Test
    public void shouldAddMember() throws Exception {
        // given

        // when
        members.register(mockMember("s1"));

        // then
//        assertThat(members.findBy("s1").isPresent(), is(true));
    }

    @Test
    public void shouldWorkWhenMemberDoesntExists() throws Exception {
        // given

        // when
//        Optional<Member> findBy = members.findBy("not existing one");

        // then
//        assertThat(findBy.isPresent(), is(false));
    }

    @Test
    public void shouldWorkWhenMemberIsNull() throws Exception {
        // given

        // when
//        Optional<Member> findBy = members.findBy(null);

        // then
//        assertThat(findBy.isPresent(), is(false));
    }

    @Test
    public void shouldUnregisterMember() throws Exception {
        // given
        Connection connection = mock(Connection.class);
        when(connection.getId()).thenReturn("s1");
        members.register(mockMember("s1"));
//        assertTrue(members.findBy("s1").isPresent());

        // when
        members.unregister(connection.getId());

        // then
//        assertFalse(members.findBy("s1").isPresent());
    }

}
