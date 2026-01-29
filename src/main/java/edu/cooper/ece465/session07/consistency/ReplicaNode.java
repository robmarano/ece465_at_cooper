package edu.cooper.ece465.session07.consistency;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session 07: Consistency
 * Represents a single storage node (Replica).
 * It simulates network latency to demonstrate consistency races.
 */
public class ReplicaNode implements KVStore {
    private static final Logger LOG = LoggerFactory.getLogger(ReplicaNode.class);
    private final String name;
    private final Map<String, String> data = new ConcurrentHashMap<>();
    private final int latencyMs;

    public ReplicaNode(String name, int latencyMs) {
        this.name = name;
        this.latencyMs = latencyMs;
    }

    @Override
    public void put(String key, String value) {
        simulateDelay();
        data.put(key, value);
        LOG.info("[{}] Wrote {}={}", name, key, value);
    }

    @Override
    public String get(String key) {
        simulateDelay();
        String val = data.get(key);
        LOG.info("[{}] Read {}={}", name, key, val);
        return val;
    }

    private void simulateDelay() {
        try {
            Thread.sleep(latencyMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
