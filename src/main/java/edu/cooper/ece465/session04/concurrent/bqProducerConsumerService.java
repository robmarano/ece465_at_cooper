package edu.cooper.ece465.session04.concurrent;

// import java.util.concurrent.ArrayBlockingQueue;
// import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class bqProducerConsumerService {
    private static final Logger LOG = LoggerFactory.getLogger(bqProducerConsumerService.class);

    public static void main(String[] args) {
        //Creating BlockingQueue of size 10
        LOG.debug("Creating new queue");
        // BlockingQueue<Message> queue = new ArrayBlockingQueue<>(10);
        ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<>();
        LOG.debug("Creating new queue - DONE");
        LOG.debug("Creating producer");
        bqProducer producer = new bqProducer(queue);
        LOG.debug("Creating producer - DONE");
        LOG.debug("Creating consumer");
        bqConsumer consumer = new bqConsumer(queue);
        LOG.debug("Creating producer - DONE");

        //starting consumer to consume messages from queue
        LOG.debug("Creating consumer thread");
        new Thread(consumer).start();
        LOG.debug("Creating consumer thread - RUNNING");

        //starting producer to produce messages in queue
        LOG.debug("Creating producer thread");
        new Thread(producer).start();
        LOG.debug("Creating producer thread - RUNNING");

        LOG.debug("Producer and Consumer has been started");
        System.out.println("Producer and Consumer has been started");
    }

}