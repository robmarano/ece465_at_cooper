package edu.cooper.ece465.session09.security;

import java.security.KeyPair;
import java.util.Base64;

/**
 * Session 09: Security
 * Demo: Confidentiality & Integrity using Digital Signatures.
 */
public class SecurityDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting Security Demo (Digital Signatures)...");

        // 1. Setup Identities
        System.out.println("Generating Keys...");
        KeyPair aliceKeys = KeyManager.generateKeyPair(); // The Sender
        KeyPair malloryKeys = KeyManager.generateKeyPair(); // The Attacker

        // --- Scenario 1: Valid Message ---
        System.out.println("\n--- Scenario 1: Legitimate Message ---");
        String message = "Transfer $100 to Bob";
        System.out.println("Alice signs message: \"" + message + "\"");

        byte[] signature = DigitalSignature.sign(message, aliceKeys.getPrivate());
        System.out.println(
                "Signature generated: " + Base64.getEncoder().encodeToString(signature).substring(0, 20) + "...");

        System.out.println("Bob receives message and verifies with Alice's Public Key...");
        boolean valid = DigitalSignature.verify(message, signature, aliceKeys.getPublic());

        if (valid) {
            System.out.println(">> SUCCESS: Message is Verified and Authentic.");
        } else {
            System.err.println(">> ERROR: Verification Failed!");
        }

        // --- Scenario 2: Tampering Attack ---
        System.out.println("\n--- Scenario 2: Man-In-The-Middle Attack (Tampering) ---");
        String tamperedMessage = "Transfer $900 to Bob";
        System.out.println("Mallory intercepts and changes message to: \"" + tamperedMessage + "\"");
        System.out.println("Bob receives TAMPERED message but ORIGINAL signature...");

        boolean tamperedValid = DigitalSignature.verify(tamperedMessage, signature, aliceKeys.getPublic());

        if (!tamperedValid) {
            System.out.println(">> SUCCESS: Tampering Detected! Signature does not match data.");
        } else {
            System.err.println(">> ERROR: Failed to detect tampering!");
        }

        // --- Scenario 3: Forgery Attack ---
        System.out.println("\n--- Scenario 3: Forgery Attack ---");
        String forgeMsg = "Buy Crypto";
        System.out.println("Mallory writes: \"" + forgeMsg + "\" and signs with HER private key.");
        byte[] forgedSig = DigitalSignature.sign(forgeMsg, malloryKeys.getPrivate());

        System.out.println("Mallory sends it to Bob claiming it's from Alice.");
        System.out.println("Bob verifies with ALICE'S Public Key...");

        boolean forgeryValid = DigitalSignature.verify(forgeMsg, forgedSig, aliceKeys.getPublic());

        if (!forgeryValid) {
            System.out.println(">> SUCCESS: Forgery Rejected! Key mismatch.");
        } else {
            System.err.println(">> ERROR: Failed to detect forgery!");
        }

        System.out.println("\nDemo Complete.");
    }
}
