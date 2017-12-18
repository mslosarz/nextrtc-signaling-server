package org.nextrtc.signalingserver.performance;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class MockedClient {
    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("CLOSED: " + statusCode + " -> " + reason);
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("STARTED: " + session);
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        System.out.println("MSG: " + msg);
    }
}
