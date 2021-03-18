package edu.cooper.ece465.apps.imaging;

import edu.cooper.ece465.utils.Utils;

import java.io.*;
import java.net.Socket;
import java.net.InetAddress;
import java.net.SocketException;
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
    private final InputStream is;
    private final OutputStream os;
    private final BufferedInputStream bis;
    private final BufferedOutputStream bos;
    private final DataInputStream dis;
    private final DataOutputStream dos;

    /**
     * ctor()
     */
    public ImagingThread(Socket socket) throws IOException {
        this.socket = socket;
        this.clientAddress = this.socket.getInetAddress();
        this.clientPort = this.socket.getPort();
        this.protocol = new ImagingProtocol(this, this.socket);
        this.is = this.socket.getInputStream();
        this.os = this.socket.getOutputStream();
        this.bis = new BufferedInputStream(this.is);
        this.bos = new BufferedOutputStream(this.os);
        this.dis = new DataInputStream(this.bis);
        this.dos = new DataOutputStream(this.bos);
    }

    private void finish() throws Exception {
        this.dis.close();
        this.dos.close();
        this.bis.close();
        this.bos.close();
        this.is.close();
        this.os.close();
        this.socket.close();
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
            LOG.debug("Completed processing commands from client {}.", this.getClientName());
            LOG.debug("Exiting {}.", this.getClass().getSimpleName());
        } catch (Exception e) {
            String errorMessage = String.format("General Exception connected to %s on port %d", this.clientAddress.toString(), this.clientPort);
            Utils.handleException(LOG, e, errorMessage);
        } finally {
            try {
                this.finish();
            } catch (EOFException e3) {
                String errorMessage = String.format("Unexpected disconnection from client: %s (%s:%d)", this.clientName,
                        this.clientAddress.toString(), this.clientPort);
                Utils.handleException(LOG, e3, errorMessage);
            } catch (SocketException e4) {
                String errorMessage = String.format("Unexpected broken pipe from client: %s (%s:%d)", this.clientName,
                        this.clientAddress.toString(), this.clientPort);
                Utils.handleException(LOG, e4, errorMessage);
            } catch (IOException e) {
                String errorMessage = String.format("IOException connected to %s on port %d", this.clientAddress.toString(), this.clientPort);
                Utils.handleException(LOG, e, errorMessage);
            } catch (Exception e1) {
                String errorMessage = "Found exception";
                Utils.handleException(LOG, e1, errorMessage);
            } catch (Error err) {
                String errorMessage = "Found error";
                Utils.handleError(LOG, err, errorMessage);
            }
    }
        LOG.debug("End thread for client: {}", this.clientName);
    }
}