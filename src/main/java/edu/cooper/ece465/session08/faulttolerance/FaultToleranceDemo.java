package edu.cooper.ece465.session08.faulttolerance;

/**
 * Session 08: Fault Tolerance
 * Demo: Primary-Backup Failover with Heartbeats.
 */
public class FaultToleranceDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting Fault Tolerance Demo (Primary-Backup)...");

        // 1. Setup Nodes
        ServiceNode primary = new ServiceNode("Node-A", ServiceNode.Role.PRIMARY);
        ServiceNode backup = new ServiceNode("Node-B", ServiceNode.Role.BACKUP);

        // 2. Link them (Simulating network connection)
        primary.setPeer(backup);
        backup.setPeer(primary); // Theoretically Primary doesn't need to know Backup for simple heartbeats, but
                                 // useful for ack

        // 3. Start Threads
        Thread t1 = new Thread(primary);
        Thread t2 = new Thread(backup);
        t1.start();
        t2.start();

        // 4. Let them sync (Warm Up)
        System.out.println("--- Phase 1: Normal Operation (Syncing) ---");
        Thread.sleep(4000);

        // 5. Simulate Failure
        System.out.println("\n--- Phase 2: FAILURE INJECTION ---");
        System.out.println(">>> KILLING PRIMARY (Node-A) <<<");
        primary.stop();
        // We do NOT stop the thread, we just tell the logic to stop running, simulating
        // a crash loop or exit.

        // 6. Observe Failover
        Thread.sleep(4000); // Wait for timeout and takeover

        // 7. Cleanup
        System.out.println("\n--- Demo Complete ---");
        backup.stop();
        t1.join();
        t2.join();
        System.exit(0);
    }
}
