package edu.cooper.ece465.concurrent;

import java.util.concurrent.BlockingQueue;

public class bqConsumer implements Runnable{

private BlockingQueue<Message> queue;
    
    public bqConsumer(BlockingQueue<Message> q){
        this.queue=q;
    }

    @Override
    public void run() {
        try{
            Message msg;
            //consuming messages until exit message is received
            while((msg = queue.take()).getMsg() !="exit"){
            Thread.sleep(10);
            System.out.println("Consumed "+msg.getMsg());
            }
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}