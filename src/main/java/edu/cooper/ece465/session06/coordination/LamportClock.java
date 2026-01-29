package edu.cooper.ece465.session06.coordination;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Session 06: Coordination
 * Topic: Logical Clocks (Tanenbaum Ch. 6.2)
 *
 * A Lamport Logical Clock.
 * Unlike physical clocks, this clock does not measure seconds/milliseconds.
 * It measures the "Order of Events".
 *
 * Property: If A -> B, then C(A) < C(B).
 */
public class LamportClock {
    private final AtomicInteger value;

    public LamportClock() {
        this.value = new AtomicInteger(0);
    }

    /**
     * Internal Event: Just increment the clock.
     * C = C + 1
     */
    public int tick() {
        return value.incrementAndGet();
    }

    /**
     * Receive Event: Update based on sender's timestamp.
     * C = max(C, messageTime) + 1
     */
    public int update(int receivedTime) {
        return value.updateAndGet(current -> Math.max(current, receivedTime) + 1);
    }

    public int getValue() {
        return value.get();
    }
}
