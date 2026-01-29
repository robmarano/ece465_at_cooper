package edu.cooper.ece465.session06.coordination;

import java.util.ArrayList;
import java.util.List;

/**
 * Session 06: Coordination
 * Topic: Simulation Driver
 *
 * Runs multiple LamportProcesses to demonstrate causal ordering.
 */
public class LamportDemo {
    public static void main(String[] args) throws InterruptedException {
        int numProcesses = 3;
        List<LamportProcess> processes = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        // Create Processes
        for (int i = 0; i < numProcesses; i++) {
            processes.add(new LamportProcess(i));
        }

        // Connect Peers (Fully Connected Mesh)
        for (int i = 0; i < numProcesses; i++) {
            for (int j = 0; j < numProcesses; j++) {
                if (i != j) {
                    processes.get(i).addPeer(processes.get(j));
                }
            }
        }

        System.out.println("Starting Lamport Clock Demo...");
        System.out.println("Property to Verify: If Receive(A), then Clock(Receiver) > Clock(Sender)");
        System.out.println("----------------------------------------------------------------------");

        // Start Threads
        for (LamportProcess p : processes) {
            Thread t = new Thread(p);
            threads.add(t);
            t.start();
        }

        // Let simulation run
        Thread.sleep(5000);

        // Stop
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Stopping simulation...");
        for (LamportProcess p : processes) {
            p.stop();
        }

        for (Thread t : threads) {
            t.join();
        }
        System.out.println("Simulation Complete.");
    }
}
