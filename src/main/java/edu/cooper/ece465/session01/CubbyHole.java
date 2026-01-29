package edu.cooper.ece465.session01;

/**
 * Session 01: Shared State
 * Topic: Synchronization & Race Conditions
 *
 * This class represents the "Shared Memory" or "Channel".
 * The 'synchronized' keyword ensures mutual exclusion, a critical concept
 * discussed in Tanenbaum Ch. 6 (Coordination), even at this thread level.
 */
public class CubbyHole {
    private int contents;
    private boolean available = false;

    public synchronized int get() {
        while (available == false) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        available = false;
        notifyAll();
        return contents;
    }

    public synchronized void put(int value) {
        while (available == true) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        contents = value;
        available = true;
        notifyAll();
    }
}