package edu.cooper.ece465.session06.coordination;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Session 06: Coordination
 * Topic: Distributed Processes & Event Ordering
 *
 * Simulates a distributed process that maintains a Logical Clock.
 * It performs three types of events:
 * 1. Internal Event
 * 2. Sending a Message
 * 3. Receiving a Message
 */
public class LamportProcess implements Runnable {
    private final int pid;
    private final LamportClock clock;
    private final List<LamportProcess> peers;
    private final Random random;
    private boolean running;

    public LamportProcess(int pid) {
        this.pid = pid;
        this.clock = new LamportClock();
        this.peers = new ArrayList<>();
        this.random = new Random();
        this.running = true;
    }

    public void addPeer(LamportProcess peer) {
        this.peers.add(peer);
    }

    public void stop() {
        this.running = false;
    }

    @Override
    public void run() {
        System.out.printf("Process %d started.\n", pid);
        while (running) {
            try {
                Thread.sleep(random.nextInt(500) + 100); // Simulate work

                int action = random.nextInt(3);
                switch (action) {
                    case 0: // Internal Event
                        int time = clock.tick();
                        System.out.printf("[PID=%d, Time=%d] Internal Event\n", pid, time);
                        break;
                    case 1: // Send Message (if peers exist)
                        if (!peers.isEmpty()) {
                            int sendTime = clock.tick();
                            LamportProcess target = peers.get(random.nextInt(peers.size()));
                            System.out.printf("[PID=%d, Time=%d] Sending message to PID=%d\n", pid, sendTime,
                                    target.pid);
                            target.receive("Hello", sendTime, this.pid);
                        }
                        break;
                    case 2: // Do nothing (wait for receive)
                        break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }

    /**
     * Simulates receiving a message from another process.
     * This is called by the SENDER thread (conceptually via RMI/Socket).
     */
    public synchronized void receive(String msg, int senderTime, int senderPid) {
        int newTime = clock.update(senderTime);
        System.out.printf("[PID=%d, Time=%d] Received message from PID=%d (MsgTime=%d)\n",
                pid, newTime, senderPid, senderTime);

        // Verification Logic:
        // Current Time MUST be > Sender Time.
        if (newTime <= senderTime) {
            System.err.printf("VIOLATION: Process %d Time (%d) <= Sender %d Time (%d)\n",
                    pid, newTime, senderPid, senderTime);
        }
    }
}
