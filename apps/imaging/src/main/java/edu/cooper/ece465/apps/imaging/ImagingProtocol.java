package edu.cooper.ece465.apps.imaging;

import edu.cooper.ece465.utils.Utils;

import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImagingProtocol {
    private static final Logger LOG = LoggerFactory.getLogger(ImagingProtocol.class);

    public static final Map<String, String> RESPONSE_MAP = new HashMap<>();
    private final Scanner input;
    private final PrintWriter output;
    protected String clientName;
    private final ImagingThread imagingThread;

    static {
        RESPONSE_MAP.put("QUIT", "Received exit/quit command.");
        RESPONSE_MAP.put("ID ", "Client registered with ID = %s");
        RESPONSE_MAP.put("INFO", "Client address = %s, port = %d");
    }

    public ImagingProtocol(ImagingThread thread, Scanner input, PrintWriter output) {
        this.imagingThread = thread;
        this.input = input;
        this.output = output;
    }

    private String getClientName() {
        this.clientName = this.imagingThread.getClientName();
        return this.clientName;
    }

    private void setClientName(String name) {
        this.clientName = name;
        this.imagingThread.setClientName(name);
    }

    /**
     * processCommands()
     */
    protected void processCommands(Scanner input, PrintWriter output) {
        LOG.debug("Processing commands from client: {}", this.clientName);

        try {
            String response;
            while (input.hasNextLine()) {
                String command = input.nextLine().toUpperCase();
                if ( (command.startsWith("QUIT")) || (command.startsWith("EXIT")) ) {
                    response = RESPONSE_MAP.get("QUIT");
                    LOG.debug(response);
                    output.println(response);
                    return;
                } else if (command.startsWith("ID ")) {
                    setClientName(command.substring(3));
                    response = String.format(RESPONSE_MAP.get("ID "), this.clientName);
                    LOG.debug(response);
                    output.println(response);
                } else if (command.startsWith("INFO")) {
                    response = String.format(RESPONSE_MAP.get("INFO"),
                        this.imagingThread.clientAddress.toString(), this.imagingThread.clientPort);
                    LOG.debug(response);
                    output.println(response);
                } else {
                    LOG.error("Command not supported: {}", command);
                }
            }
        } catch (Exception e1) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e1.printStackTrace(pw);
            LOG.error(String.format("Found Exception.",e1));
            LOG.error(pw.toString());
        }
        LOG.debug("Finished processing commands from client: {}", this.clientName);
    }



    // public String processInput(String theInput) {
    //     String theOutput = null;

    //     if (state == WAITING) {
    //         theOutput = "Knock! Knock!";
    //         state = SENTKNOCKKNOCK;
    //     } else if (state == SENTKNOCKKNOCK) {
    //         if (theInput.equalsIgnoreCase("Who's there?")) {
    //             theOutput = clues[currentJoke];
    //             state = SENTCLUE;
    //         } else {
    //             theOutput = "You're supposed to say \"Who's there?\"! " +
			 //    "Try again. Knock! Knock!";
    //         }
    //     } else if (state == SENTCLUE) {
    //         if (theInput.equalsIgnoreCase(clues[currentJoke] + " who?")) {
    //             theOutput = answers[currentJoke] + " Want another? (y/n)";
    //             state = ANOTHER;
    //         } else {
    //             theOutput = "You're supposed to say \"" + 
			 //    clues[currentJoke] + 
			 //    " who?\"" + 
			 //    "! Try again. Knock! Knock!";
    //             state = SENTKNOCKKNOCK;
    //         }
    //     } else if (state == ANOTHER) {
    //         if (theInput.equalsIgnoreCase("y")) {
    //             theOutput = "Knock! Knock!";
    //             if (currentJoke == (NUMJOKES - 1))
    //                 currentJoke = 0;
    //             else
    //                 currentJoke++;
    //             state = SENTKNOCKKNOCK;
    //         } else {
    //             theOutput = "Bye.";
    //             state = WAITING;
    //         }
    //     }
    //     return theOutput;
    // }
}