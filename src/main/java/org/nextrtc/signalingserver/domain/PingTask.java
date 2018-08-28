package org.nextrtc.signalingserver.domain;

public class PingTask implements Runnable {

    private Member to;

    public PingTask(Connection to) {
        this.to = new Member(to, null);
    }

    @Override
    public void run() {
        if (Thread.interrupted() || !to.getConnection().isOpen()) {
            return;
        }
        to.send(InternalMessage.create()
                .to(to)
                .signal(Signal.PING)
                .build()
        );
    }

}
