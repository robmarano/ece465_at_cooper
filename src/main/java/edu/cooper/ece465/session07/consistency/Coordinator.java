package edu.cooper.ece465.session07.consistency;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session 07: Consistency
 * Manages writes to multiple replicas.
 * Implements Strong vs Eventual consistency logic.
 */
public class Coordinator implements KVStore {
    private static final Logger LOG = LoggerFactory.getLogger(Coordinator.class);
    private final List<ReplicaNode> replicas;
    private final ConsistencyMode mode;
    private final ExecutorService backgroundPool = Executors.newCachedThreadPool();
    private final Random random = new Random();

    public enum ConsistencyMode {
        STRONG, EVENTUAL
    }

    public Coordinator(List<ReplicaNode> replicas, ConsistencyMode mode) {
        this.replicas = replicas;
        this.mode = mode;
    }

    public void shutdown() {
        backgroundPool.shutdownNow();
    }

    @Override
    public void put(String key, String value) {
        LOG.info("Client PUT {}={} (Mode={})", key, value, mode);
        if (mode == ConsistencyMode.STRONG) {
            // Write to ALL synchronously
            for (ReplicaNode node : replicas) {
                node.put(key, value);
            }
        } else {
            // Write to ONE synchronously
            ReplicaNode primary = replicas.get(0);
            primary.put(key, value);

            // Replicate to others asynchronously (simulating eventual)
            for (int i = 1; i < replicas.size(); i++) {
                ReplicaNode follower = replicas.get(i);
                backgroundPool.submit(() -> follower.put(key, value));
            }
        }
    }

    @Override
    public String get(String key) {
        // Simple strategy: Read from a random replica
        // In Strong mode, this is fine because all are updated.
        // In Eventual mode, this might hit a stale replica.
        ReplicaNode node = replicas.get(random.nextInt(replicas.size()));
        return node.get(key);
    }
}
