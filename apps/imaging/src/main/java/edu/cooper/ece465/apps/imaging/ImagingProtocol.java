package edu.cooper.ece465.apps.imaging;

import edu.cooper.ece465.utils.Utils;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImagingProtocol {
    private static final Logger LOG = LoggerFactory.getLogger(ImagingProtocol.class);

    public static final Map<String, String> RESPONSE_MAP = new HashMap<>();
    public static final Map<String, String> REGEX_MAP = new HashMap<>();
    public static final Map<String, Pattern> PATTERN_MAP = new HashMap<>();
    public static final Map<String, Matcher> MATCHER_MAP = new HashMap<>();
    private final Scanner scanner;
    private final PrintWriter printWriter;
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    protected String clientName;
    private final ImagingThread imagingThread;

    private DataOutputStream dataOutputStream = null;
    private DataInputStream dataInputStream = null;


    static {
        RESPONSE_MAP.put("QUIT", "BYE"); //"Received exit/quit command.");
        RESPONSE_MAP.put("ID", "Client registered with ID = %s");
        RESPONSE_MAP.put("INFO", "Client address = %s, port = %d");
        RESPONSE_MAP.put("FIND", "Find file %s");
        RESPONSE_MAP.put("GET", "Client ID %s (%s:%s) requests file %s");
        RESPONSE_MAP.put("SEND", "Send file %s to client ID %s (%s:%s)");

        REGEX_MAP.put("QUIT", "[Qq][Uu][Ii][Tt]");
        REGEX_MAP.put("ID", "[Ii][Dd]");
        REGEX_MAP.put("INFO", "[Ii][Nn][Ff][Oo]");
        REGEX_MAP.put("FIND", "[Ff][Ii][Nn][Dd]");
        REGEX_MAP.put("GET", "[Gg][Ee][Tt]");
        REGEX_MAP.put("SEND", "[Ss][Ee][Nn][Dd]");

        Pattern patternQUIT = Pattern.compile(REGEX_MAP.get("QUIT"));
        PATTERN_MAP.put("QUIT", patternQUIT);
        Pattern patternID = Pattern.compile(REGEX_MAP.get("ID"));
        PATTERN_MAP.put("ID", patternID);
        Pattern patternINFO = Pattern.compile(REGEX_MAP.get("INFO"));
        PATTERN_MAP.put("INFO", patternINFO);
        Pattern patternFIND = Pattern.compile(REGEX_MAP.get("FIND"));
        PATTERN_MAP.put("FIND", patternFIND);
        Pattern patternGET = Pattern.compile(REGEX_MAP.get("GET"));
        PATTERN_MAP.put("GET", patternGET);
        Pattern patternSEND = Pattern.compile(REGEX_MAP.get("SEND"));
        PATTERN_MAP.put("SEND", patternSEND);
    }

    public ImagingProtocol(ImagingThread thread, Socket socket) throws IOException {
        this.imagingThread = thread;
        this.socket = socket;
        this.inputStream = this.socket.getInputStream();
        this.outputStream = this.socket.getOutputStream();
        this.scanner = new Scanner(this.inputStream);
        this.printWriter = new PrintWriter(this.outputStream, true);
        this.dataInputStream = new DataInputStream(this.inputStream);
        this.dataOutputStream = new DataOutputStream(this.outputStream);
        this.clientName = thread.clientName;
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
    protected void processCommands() {
        LOG.debug("Processing commands from client: {}", this.clientName);

        String response = "";
        String fileName = "";
        try {
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();
                setUpMatchers(command);
                if ( MATCHER_MAP.get("QUIT").find() ) {
                    response = RESPONSE_MAP.get("QUIT");
                    LOG.debug(response);
                    printWriter.println(response);
                    return;
                } else if ( MATCHER_MAP.get("ID").find() ) {
                    setClientName(command.substring(3));
                    response = String.format(RESPONSE_MAP.get("ID"), this.clientName);
                    LOG.debug(response);
                    printWriter.println(response);
                } else if ( MATCHER_MAP.get("INFO").find() ) {
                    response = String.format(RESPONSE_MAP.get("INFO"),
                        this.imagingThread.clientAddress.toString(), this.imagingThread.clientPort);
                    LOG.debug(response);
                    printWriter.println(response);
                } else if ( MATCHER_MAP.get("FIND").find() ) {
                    fileName = command.substring(5);
                    response = String.format(RESPONSE_MAP.get("FIND"), fileName);
                    LOG.debug(response);
                    printWriter.println(response);
                } else if ( MATCHER_MAP.get("GET").find() ) {
                    fileName = command.substring(4);
                    response = String.format(RESPONSE_MAP.get("GET"),fileName, this.clientName,
                        this.imagingThread.clientAddress.toString(), this.imagingThread.clientPort);
                    LOG.debug(response);
                    printWriter.println(response);
                    printWriter.println(String.format("RECEIVE %s", fileName));
                    printWriter.println(String.format("Send file %s to client ID %s (%s:%s)",fileName, this.clientName,
                        this.imagingThread.clientAddress.toString(), this.imagingThread.clientPort));
                    Utils.sendFile(fileName, dataOutputStream);
                    printWriter.println(String.format("Sent file %s to client ID %s (%s:%s)",fileName, this.clientName,
                        this.imagingThread.clientAddress.toString(), this.imagingThread.clientPort));
                    dataOutputStream.flush();
                } else {
                    LOG.error("Command not supported: {}", command);
                }
            }
        } catch (FileNotFoundException e2) {
            String errorMessage = String.format("File not found: %s", fileName);
            Utils.handleException(LOG, e2, errorMessage);
        } catch (Exception e1) {
            String errorMessage = "Found exception";
            Utils.handleException(LOG, e1, errorMessage);
        }
        LOG.debug("Finished processing commands from client: {}", this.clientName);
    }

    private void setUpMatchers(String command) {
        Matcher matcherQUIT = PATTERN_MAP.get("QUIT").matcher(command);
        MATCHER_MAP.put("QUIT", matcherQUIT);
        Matcher matcherID = PATTERN_MAP.get("ID").matcher(command);
        MATCHER_MAP.put("ID", matcherID);
        Matcher matcherINFO = PATTERN_MAP.get("INFO").matcher(command);
        MATCHER_MAP.put("INFO", matcherINFO);
        Matcher matcherFIND = PATTERN_MAP.get("FIND").matcher(command);
        MATCHER_MAP.put("FIND", matcherFIND);
        Matcher matcherGET = PATTERN_MAP.get("GET").matcher(command);
        MATCHER_MAP.put("GET", matcherGET);
        Matcher matcherSEND = PATTERN_MAP.get("SEND").matcher(command);
        MATCHER_MAP.put("SEND", matcherSEND);
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