package org.nextrtc.signalingserver.performance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.eclipse.jetty.websocket.client.WebSocketClient;

@Getter
@AllArgsConstructor
public class Tuple<T> {
    private WebSocketClient client;
    private T socket;
}
