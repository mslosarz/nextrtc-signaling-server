package org.nextrtc.signalingserver.domain;

import javax.websocket.Session;

public class PingTask implements Runnable {

    private Member to;

    public PingTask(Session to) {
        this.to = new Member(to, null);
    }

    @Override
    public void run() {
        InternalMessage.create()//
                .to(to)//
                .signal(Signal.PING)//
                .build()//
                .sendCarefully();
    }

}
