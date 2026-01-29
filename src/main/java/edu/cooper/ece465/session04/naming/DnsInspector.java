package edu.cooper.ece465.session04.naming;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DnsInspector {
    private static final Logger LOG = LoggerFactory.getLogger(DnsInspector.class);

    public static void main(String[] args) {
        String domain = args.length > 0 ? args[0] : "www.google.com";
        LOG.info("Inspecting DNS for domain: {}", domain);

        try {
            long startTime = System.currentTimeMillis();
            InetAddress[] addresses = InetAddress.getAllByName(domain);
            long duration = System.currentTimeMillis() - startTime;

            LOG.info("Resolution took {} ms", duration);
            for (InetAddress addr : addresses) {
                LOG.info(" - Address: {}", addr.getHostAddress());
                LOG.info(" - Host Name: {}", addr.getHostName());
                LOG.info(" - Canonical Host Name: {}", addr.getCanonicalHostName());
            }

            // Demonstrate caching
            LOG.info("--- Second Lookup (Demonstrating Cache) ---");
            startTime = System.currentTimeMillis();
            InetAddress.getAllByName(domain);
            duration = System.currentTimeMillis() - startTime;
            LOG.info("Second resolution took {} ms", duration);

        } catch (UnknownHostException e) {
            LOG.error("Could not resolve domain: {}", domain);
        }
    }
}
