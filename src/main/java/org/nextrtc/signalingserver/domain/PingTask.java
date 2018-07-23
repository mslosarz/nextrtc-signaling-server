package org.nextrtc.signalingserver.domain;

public class PingTask implements Runnable {

    private MessageSender sender;
    private Member to;

    public PingTask(Connection to, MessageSender sender) {
        this.to = new Member(to, null);
        this.sender = sender;
    }

    @Override
    public void run() {
        if(Thread.interrupted()){
            return;
        }
        sender.send(InternalMessage.create()//
                .to(to)//
                .signal(Signal.PING)//
                .build()//
        );
    }

}
