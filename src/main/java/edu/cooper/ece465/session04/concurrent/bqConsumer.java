package edu.cooper.ece465.session04.concurrent;

import edu.cooper.ece465.commons.utils.Utils;

// import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

public class bqConsumer implements Runnable {
    private final Logger LOG = Logger.getLogger(bqConsumer.class);

    // private BlockingQueue<Message> queue;
    private ConcurrentLinkedQueue<Message> queue;

    // public bqConsumer(BlockingQueue<Message> q){
    public bqConsumer(ConcurrentLinkedQueue<Message> q){
        LOG.debug("bqConsumer - ctor");
        this.queue=q;
        LOG.debug("bqConsumer - ctor - DONE");
    }

    @Override
    public void run() {
        LOG.debug("bqConsumer - run() - START");
        try{
            Message msg;
            //consuming messages until exit message is received
            LOG.debug("Looping through through messages on the queue - START");
            // while((msg = queue.take()).getMsg() != "exit"){
            do {
                msg = queue.poll();
                if (msg != null) {
                    String message = msg.getMsg();
                    LOG.debug(String.format("Consumed MESSAGE = %s",msg.getMsg()));
                    if (message.equals("exit")) {
                        LOG.debug(String.format("ENDING %s",msg.getMsg()));
                        break;
                    }
                }
                Thread.sleep(250);
            }
            while (true);
            LOG.debug("Looping through through messages on the queue - END");
        } catch(NoSuchElementException ex1) {
            String errorMessage = String.format("Encountered a NoSuchFieldException: %s", ex1.getMessage());
            Utils.handleException(LOG, ex1, errorMessage);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        LOG.debug("bqConsumer - run() - END");
    }
}