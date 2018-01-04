package org.nextrtc.signalingserver.domain;

import javax.websocket.Session;

public class PingTask implements Runnable {

    private MessageSender sender;
    private Member to;

    public PingTask(Session to, MessageSender sender) {
        this.to = new Member(to, null);
        this.sender = sender;
    }

    @Override
    public void run() {
        sender.send(InternalMessage.create()//
                .to(to)//
                .signal(Signal.PING)//
                .build()//
        );
    }

}
