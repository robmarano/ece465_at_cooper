package edu.cooper.ece465.session08.faulttolerance;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session 08: Fault Tolerance
 * Role: A node that can act as Primary or Backup.
 *
 * Concepts:
 * - Heartbeats: Periodic signals to indicate liveness.
 * - Failover: Backup promoting itself when Primary is silent.
 */
public class ServiceNode implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceNode.class);
    private final String name;
    private Role role;
    private ServiceNode peer; // The other node

    // State
    private final AtomicInteger processedRequests = new AtomicInteger(0);
    private final AtomicBoolean running = new AtomicBoolean(true);

    // Heartbeat Config
    private long lastHeartbeatTime = System.currentTimeMillis();
    private static final long HEARTBEAT_INTERVAL = 1000;
    private static final long TIMEOUT_THRESHOLD = 2500; // 2.5x interval

    public enum Role {
        PRIMARY, BACKUP
    }

    public ServiceNode(String name, Role initialRole) {
        this.name = name;
        this.role = initialRole;
    }

    public void setPeer(ServiceNode peer) {
        this.peer = peer;
    }

    public void stop() {
        running.set(false);
        LOG.warn("[{}] CRASHED/STOPPED!", name);
    }

    public void receiveHeartbeat(int primaryCounter) {
        if (!running.get())
            return;

        if (role == Role.BACKUP) {
            lastHeartbeatTime = System.currentTimeMillis();
            // Synchronization: Backup keeps its state in sync with Primary
            processedRequests.set(primaryCounter);
            LOG.info("[{}] Received HEARTBEAT. Synced counter to {}", name, primaryCounter);
        }
    }

    @Override
    public void run() {
        LOG.info("[{}] Started as {}", name, role);

        while (running.get()) {
            try {
                Thread.sleep(500); // Tick

                if (role == Role.PRIMARY) {
                    // Do work
                    int currentWork = processedRequests.incrementAndGet();
                    LOG.info("[{}] Processing Request #{}", name, currentWork);

                    // Send Heartbeat
                    if (System.currentTimeMillis() % HEARTBEAT_INTERVAL < 500 && peer != null) {
                        peer.receiveHeartbeat(currentWork);
                    }

                } else {
                    // Role == BACKUP
                    // Monitor Watchdog
                    long now = System.currentTimeMillis();
                    if (now - lastHeartbeatTime > TIMEOUT_THRESHOLD) {
                        LOG.error("[{}] TIMEOUT! Primary is dead. (Last seen {}ms ago)", name,
                                (now - lastHeartbeatTime));
                        promoteToPrimary();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void promoteToPrimary() {
        LOG.info("------------------------------------------------");
        LOG.info("[{}] PROMOTING SELF TO PRIMARY!", name);
        LOG.info("------------------------------------------------");
        role = Role.PRIMARY;
        // Reset state or take ownership if needed
    }
}
