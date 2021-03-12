package edu.cooper.ece465.apps.imaging;

import edu.cooper.ece465.utils.Utils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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
    protected String clientName;
    private final ImagingThread imagingThread;
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final DataInputStream dis;
    private final DataOutputStream dos;
    private final BufferedInputStream bis;
    private final BufferedOutputStream bos;

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
        this.clientName = thread.clientName;
        this.imagingThread = thread;
        this.socket = socket;
        this.inputStream = this.socket.getInputStream();
        this.outputStream = this.socket.getOutputStream();
        this.bis = new BufferedInputStream(this.inputStream);
        this.bos = new BufferedOutputStream(this.outputStream);
        this.dis = new DataInputStream(this.bis);
        this.dos = new DataOutputStream(this.bos);
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
        LOG.debug("Processing commands from client: {}", this.imagingThread.clientAddress.toString());

        String response = "";
        String fileName = "";
        String command;
        String commandNotSupported = "Command not supported: %s";
        try {
//            dos.writeUTF("WELCOME"); // start protocol off with client-side
            LOG.debug("Awaiting command from client {} ({}:{})", this.clientName,
                    this.imagingThread.clientAddress.toString(), this.imagingThread.clientPort);
            command = dis.readUTF();
            while (command != null) {
                LOG.info("User command received: {}", command);
                setUpMatchers(command);
                if (MATCHER_MAP.get("QUIT").find()) {
                    response = RESPONSE_MAP.get("QUIT");
                    LOG.debug(response);
                    break;
                } else if (MATCHER_MAP.get("ID").find()) {
                    setClientName(command.substring(3));
                    response = String.format(RESPONSE_MAP.get("ID"), this.clientName);
                    LOG.debug(response);
                    dos.writeUTF(response);
                    dos.flush();
                } else if (MATCHER_MAP.get("INFO").find()) {
                    response = String.format(RESPONSE_MAP.get("INFO"),
                            this.imagingThread.clientAddress.toString(), this.imagingThread.clientPort);
                    LOG.debug(response);
                    dos.writeUTF(response);
                    dos.flush();
                } else if (MATCHER_MAP.get("FIND").find()) {
                    fileName = command.substring(5);
                    response = String.format(RESPONSE_MAP.get("FIND"), fileName);
                    LOG.debug(response);
                    dos.writeUTF(response);
                    dos.flush();
                } else if (MATCHER_MAP.get("GET").find()) {
                    fileName = command.substring(4);
                    response = String.format(RESPONSE_MAP.get("GET"), fileName, this.clientName,
                            this.imagingThread.clientAddress.toString(), this.imagingThread.clientPort);
                    LOG.debug(response);
                    dos.writeUTF(String.format("RECEIVE %s", fileName));
                    dos.flush();
                    LOG.debug(String.format("Sending file %s to client ID %s (%s:%s)", fileName, this.clientName,
                            this.imagingThread.clientAddress.toString(), this.imagingThread.clientPort));
                    Utils.sendFile(fileName, this.dos);
                    LOG.debug(String.format("Sent file %s to client ID %s (%s:%s)", fileName, this.clientName,
                            this.imagingThread.clientAddress.toString(), this.imagingThread.clientPort));
                } else if (MATCHER_MAP.get("RECEIVE").find()) {
                    fileName = command.substring(8);
                    LOG.info("Receiving file: {}", fileName);
                    int bufferSize = this.socket.getReceiveBufferSize();
                    Utils.receiveFile(fileName, dis, bufferSize);
                    LOG.info("Finished receiving file {} from server", fileName);
                } else {
                    response = String.format(commandNotSupported, command);
                    LOG.error(response);
                    dos.writeUTF(response);
                    dos.flush();
                }
                command = dis.readUTF();
            }
            LOG.debug("Finished processing commands from client: {}", this.clientName);
        } catch (EOFException e3) {
            String errorMessage = String.format("Unexpected disconnection from client: %s (%s:%d)", this.clientName,
                    this.imagingThread.clientAddress.toString(), this.imagingThread.clientPort);
            Utils.handleException(LOG, e3, errorMessage);
        } catch (SocketException e4) {
            String errorMessage = String.format("Unexpected broken pipe from client: %s (%s:%d)", this.clientName,
                    this.imagingThread.clientAddress.toString(), this.imagingThread.clientPort);
            Utils.handleException(LOG, e4, errorMessage);
        } catch (FileNotFoundException e2) {
            String errorMessage = String.format("File not found: %s", fileName);
            Utils.handleException(LOG, e2, errorMessage);
        } catch (Exception e1) {
            String errorMessage = "Found exception";
            Utils.handleException(LOG, e1, errorMessage);
        } catch (Error err) {
            String errorMessage = "Found error";
            Utils.handleError(LOG, err, errorMessage);
        }
    }

    public void processCommand(String command) throws Exception {
        this.dos.writeUTF(command);
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
}