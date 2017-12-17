package org.nextrtc.signalingserver.domain;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SessionWrapper implements Session {

    private final Session session;

    public SessionWrapper(Session session) {
        this.session = session;
    }

    @Override
    public WebSocketContainer getContainer() {
        return session.getContainer();
    }

    @Override
    public void addMessageHandler(MessageHandler messageHandler) throws IllegalStateException {
        session.addMessageHandler(messageHandler);
    }

    @Override
    public <T> void addMessageHandler(Class<T> aClass, MessageHandler.Whole<T> whole) {
        session.addMessageHandler(aClass, whole);
    }

    @Override
    public <T> void addMessageHandler(Class<T> aClass, MessageHandler.Partial<T> partial) {
        session.addMessageHandler(aClass, partial);
    }

    @Override
    public Set<MessageHandler> getMessageHandlers() {
        return session.getMessageHandlers();
    }

    @Override
    public void removeMessageHandler(MessageHandler messageHandler) {
        session.removeMessageHandler(messageHandler);
    }

    @Override
    public String getProtocolVersion() {
        return session.getProtocolVersion();
    }

    @Override
    public String getNegotiatedSubprotocol() {
        return session.getNegotiatedSubprotocol();
    }

    @Override
    public List<Extension> getNegotiatedExtensions() {
        return session.getNegotiatedExtensions();
    }

    @Override
    public boolean isSecure() {
        return session.isSecure();
    }

    @Override
    public boolean isOpen() {
        return session.isOpen();
    }

    @Override
    public long getMaxIdleTimeout() {
        return session.getMaxIdleTimeout();
    }

    @Override
    public void setMaxIdleTimeout(long l) {
        session.setMaxIdleTimeout(l);
    }

    @Override
    public void setMaxBinaryMessageBufferSize(int i) {
        session.setMaxBinaryMessageBufferSize(i);
    }

    @Override
    public int getMaxBinaryMessageBufferSize() {
        return session.getMaxBinaryMessageBufferSize();
    }

    @Override
    public void setMaxTextMessageBufferSize(int i) {
        session.setMaxTextMessageBufferSize(i);
    }

    @Override
    public int getMaxTextMessageBufferSize() {
        return session.getMaxTextMessageBufferSize();
    }

    @Override
    public RemoteEndpoint.Async getAsyncRemote() {
        return session.getAsyncRemote();
    }

    @Override
    public RemoteEndpoint.Basic getBasicRemote() {
        return session.getBasicRemote();
    }

    @Override
    public String getId() {
        return session.getId();
    }

    @Override
    public void close() throws IOException {
        session.close();
    }

    @Override
    public void close(CloseReason closeReason) throws IOException {
        session.close(closeReason);
    }

    @Override
    public URI getRequestURI() {
        return session.getRequestURI();
    }

    @Override
    public Map<String, List<String>> getRequestParameterMap() {
        return session.getRequestParameterMap();
    }

    @Override
    public String getQueryString() {
        return session.getQueryString();
    }

    @Override
    public Map<String, String> getPathParameters() {
        return session.getPathParameters();
    }

    @Override
    public Map<String, Object> getUserProperties() {
        return session.getUserProperties();
    }

    @Override
    public Principal getUserPrincipal() {
        return session.getUserPrincipal();
    }

    @Override
    public Set<Session> getOpenSessions() {
        return session.getOpenSessions();
    }

    @Override
    public boolean equals(Object obj) {
        return session.equals(obj);
    }

    @Override
    public int hashCode() {
        return session.hashCode();
    }

    @Override
    public String toString() {
        return session.toString();
    }
}
