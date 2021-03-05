package edu.cooper.ece465.apps.imaging;

import edu.cooper.ece465.utils.Utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImagingThread implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ImagingThread.class);
    private final Socket socket;
    protected final InetAddress clientAddress;
    protected final int clientPort;
    private final ImagingProtocol protocol;
    protected String clientName = "initial";
    private Scanner input;
    private PrintWriter output;

    /**
     * ctor()
     */
    public ImagingThread(Socket socket) throws IOException {
        this.socket = socket;
        this.clientAddress = this.socket.getInetAddress();
        this.clientPort = this.socket.getPort();
        this.protocol = new ImagingProtocol(this, this.socket);
        this.output = new PrintWriter(this.socket.getOutputStream(), true);       
        output.println("WELCOME");
    }

    protected synchronized String getClientName() {
        return this.clientName;
    }

    protected synchronized void setClientName(String name) {
        this.clientName = name;
    }


    /**
     * run()
     */
    @Override
    public void run() {
        LOG.debug("Starting thread for new client");
        try {
            this.protocol.processCommands();
            LOG.debug("Exiting.");
        } catch (Exception e) {
            String errorMessage = String.format("General Exception connected to %s on port %d", this.clientAddress.toString(), this.clientPort);
            Utils.handleException(LOG, e, errorMessage);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                String errorMessage = String.format("IOException connected to %s on port %d", this.clientAddress.toString(), this.clientPort);
                Utils.handleException(LOG, e, errorMessage);
            }
        }
        LOG.debug("End thread for client: {}", this.clientName);
    }
}