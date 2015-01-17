package org.nextrtc.signalingserver.api;

import javax.websocket.*;

import lombok.extern.log4j.Log4j;

import org.nextrtc.signalingserver.domain.Message;
import org.nextrtc.signalingserver.domain.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

@Log4j
@Component
public class NextRTCEndpoint {

	@Autowired
	private Server server;

	public NextRTCEndpoint() {
		WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
		if (ctx != null) {
			server = ctx.getBean(Server.class);
		}
	}

	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		log.info("Opening: " + session.getId());
		server.register(session);
	}

	@OnMessage
	public void onMessage(Message message, Session session) {
		log.info("Handling message from: " + session.getId());
		server.handle(message, session);
	}

	@OnClose
	public void onClose(Session session, CloseReason reason) {
		log.info("Closing: " + session.getId() + " with reason: " + reason.getReasonPhrase());
		server.unregister(session, reason);
	}

	@OnError
	public void onError(Session session, Throwable exception) {
		log.info("Occured exception for session: " + session.getId());
		log.error(exception);
		server.handleError(session, exception);
	}
}
