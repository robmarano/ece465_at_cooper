package edu.cooper.ece465.apps.imaging;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
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
    private String clientName = "initial";
    private Scanner input;
    private PrintWriter output;

    /**
     * ctor()
     */
    public ImagingThread(Socket socket) throws IOException {
        this.socket = socket;
        this.input = new Scanner(this.socket.getInputStream());
        this.output = new PrintWriter(this.socket.getOutputStream(), true);
        this.clientAddress = this.socket.getInetAddress();
        this.clientPort = this.socket.getPort();
        this.protocol = new ImagingProtocol(this, this.input, this.output);            
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
            this.protocol.processCommands(input, output);
            LOG.debug("Exiting.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
        LOG.debug("End thread for client: {}", this.clientName);
    }

    // @Override
    // public void run() {

    //     try (
    //         PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    //         BufferedReader in = new BufferedReader(
    //             new InputStreamReader(
    //                 socket.getInputStream()));
    //     ) {
    //         String inputLine, outputLine;
    //         ImagingProtocol dip = new ImagingProtocol();
    //         outputLine = dip.processInput(null);
    //         out.println(outputLine);

    //         while ((inputLine = in.readLine()) != null) {
    //             outputLine = dip.processInput(inputLine);
    //             out.println(outputLine);
    //             if (outputLine.equals("Bye"))
    //                 break;
    //         }
    //         socket.close();
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }
}