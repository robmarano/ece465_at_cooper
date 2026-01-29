package edu.cooper.ece465.session04.concurrent;

// import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class bqProducer implements Runnable {
    private final static int MAX_QUEUE_SIZE = 10;
    private final Logger LOG = LoggerFactory.getLogger(bqProducer.class);

    // private BlockingQueue<Message> queue;
    private ConcurrentLinkedQueue<Message> queue;

    // public bqProducer(BlockingQueue<Message> q){
    public bqProducer(ConcurrentLinkedQueue<Message> q){
        LOG.debug("bqProducer - ctor");
        this.queue=q;
        LOG.debug("bqProducer - ctor - DONE");
    }
    @Override
    public void run() {
        LOG.debug("bqProducer - run() - START");
        //produce messages
        for(int i=0; i<MAX_QUEUE_SIZE; i++){
            Message msg = new Message(""+i);
            try {
                Thread.sleep(1000);
                LOG.debug("Putting message on the queue - START");
                // queue.put(msg);
                queue.add(msg);
                LOG.debug("Putting message on the queue - END");
                LOG.debug(String.format("Produced MESSAGE = %s",msg.getMsg()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //adding exit message
        Message msg = new Message("exit");
        try {
            // queue.put(msg);
            queue.add(msg);
        // } catch (InterruptedException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.debug("bqProducer - run() - END");
    }

}