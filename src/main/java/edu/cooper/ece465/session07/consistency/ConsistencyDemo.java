package edu.cooper.ece465.session07.consistency;

import java.util.Arrays;
import java.util.List;

/**
 * Session 07: Consistency
 * Demo: Comparing Strong vs Eventual Consistency.
 */
public class ConsistencyDemo {
    public static void main(String[] args) throws InterruptedException {
        // Setup Replicas with simulated latency (e.g., 50ms)
        ReplicaNode r1 = new ReplicaNode("Replica-1", 50);
        ReplicaNode r2 = new ReplicaNode("Replica-2", 50);
        ReplicaNode r3 = new ReplicaNode("Replica-3", 50);
        List<ReplicaNode> nodes = Arrays.asList(r1, r2, r3);

        System.out.println("\n--- Experiment 1: STRONG Consistency ---");
        // Strong Mode: Reads should always see the latest write
        Coordinator strongCoord = new Coordinator(nodes, Coordinator.ConsistencyMode.STRONG);
        strongCoord.put("key1", "Available");

        System.out.println("Reading key1...");
        String val1 = strongCoord.get("key1");
        System.out.println("Result: " + val1); // Should be "Available"

        // --- Experiment 2: EVENTUAL Consistency ---
        System.out.println("\n--- Experiment 2: EVENTUAL Consistency ---");
        // Clean slate logic for demo simplicity, or just update same nodes
        Coordinator weakCoord = new Coordinator(nodes, Coordinator.ConsistencyMode.EVENTUAL);

        // This returns quickly (after updating R1), but R2/R3 might still be empty
        weakCoord.put("key2", "Propagating");

        System.out.println("Reading key2 immediately...");
        // This reads from a random node. 2/3 chance of hitting R2/R3 which might be
        // stale (null)
        String val2 = weakCoord.get("key2");
        System.out.println("Result: " + val2);

        if (val2 == null) {
            System.out.println(">> OBSERVED STALE READ (Expected in weak consistency)");
        } else {
            System.out.println(">> Got lucky (Hit the primary or fast propagation)");
        }

        Thread.sleep(1000); // Wait for propagation
        System.out.println("Reading key2 after 1s...");
        System.out.println("Result: " + weakCoord.get("key2")); // Should be consistent now

        strongCoord.shutdown();
        weakCoord.shutdown();
        System.exit(0);
    }
}
