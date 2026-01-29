package edu.cooper.ece465.session01;

import java.util.Random;

/**
 * Session 01: The Distributed Transition
 * Topic: Threads & Concurrency (Tanenbaum Ch. 3.2)
 *
 * This class represents a "Producer" thread. In a distributed system,
 * components often operate concurrently, producing and consuming data.
 * This example demonstrates shared state synchronization within a single
 * process.
 */
public class Producer implements Runnable {
    private Drop drop;

    public Producer(Drop drop) {
        this.drop = drop;
    }

    public void run() {
        String importantInfo[] = {
                "Mares eat oats",
                "Does eat oats",
                "Little lambs eat ivy",
                "A kid will eat ivy too"
        };
        Random random = new Random();

        for (int i = 0; i < importantInfo.length; i++) {
            drop.put(importantInfo[i]);
            try {
                Thread.sleep(random.nextInt(5000));
            } catch (InterruptedException e) {
            }
        }
        drop.put("DONE");
    }
}
