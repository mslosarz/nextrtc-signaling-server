package org.nextrtc.signalingserver.repository;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.websocket.Session;

import org.junit.Test;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.repository.Members;

import com.google.common.base.Optional;

public class MembersTest {

	private Members members = new Members();

	@Test
	public void shouldAddMember() throws Exception {
		// given
		Session session = mock(Session.class);
		when(session.getId()).thenReturn("s1");

		// when
		members.register(new Member(session));

		// then
		assertThat(members.findBy("s1").isPresent(), is(true));
	}

	@Test
	public void shouldWorkWhenMemberDoesntExists() throws Exception {
		// given

		// when
		Optional<Member> findBy = members.findBy("not existing one");

		// then
		assertThat(findBy.isPresent(), is(false));
	}

	@Test
	public void shouldWorkWhenMemberIsNull() throws Exception {
		// given

		// when
		Optional<Member> findBy = members.findBy(null);

		// then
		assertThat(findBy.isPresent(), is(false));
	}

	@Test
	public void shouldUnregisterMember() throws Exception {
		// given
		Session session = mock(Session.class);
		when(session.getId()).thenReturn("s1");
		members.register(new Member(session));
		assertTrue(members.findBy("s1").isPresent());

		// when
		members.unregister("s1");

		// then
		assertFalse(members.findBy("s1").isPresent());
	}

}
