# DEV.ms
## Scratch code

```java

package edu.cooper.ece465.apps.imaging;

import edu.cooper.ece465.utils.Utils;

import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import veddan.physicalcores.PhysicalCores;

public class ImagingClient { // implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ImagingClient.class);
    public static final String PROP_FILE_NAME = "imaging.properties";
    public static final String APP_VERSION;
    public static final boolean USE_ALL_CORES;
    public static final int THREAD_POOL_SIZE;
    public static final int SERVICE_PORT;

//    private final ServerSocket serverSocket;
//    private final Socket clientSocket;
//    private final ExecutorService servicePool, clientPool;

    static {
        Properties props = new Properties();
        String appVersion = "app.version";
        boolean useAllCores = false;
        int threadPoolSize = 2;
        int servicePort = 1859;
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream(PROP_FILE_NAME);
            props.load(is);
            Utils.printProperties(props, LOG);
            appVersion = props.getProperty("app.version");
            servicePort = Integer.parseInt(props.getProperty("service.port"));
            useAllCores = Boolean.getBoolean("cores.all");
            int numCores = PhysicalCores.physicalCoreCount().intValue();
            int poolSize = Integer.parseInt(props.getProperty("pool.size"));
            threadPoolSize = useAllCores ? numCores : poolSize;
        } catch (NullPointerException e0) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e0.printStackTrace(pw);
            LOG.error(String.format("Null pointer exception initializing loading of properties file: %s", PROP_FILE_NAME),e0);
            LOG.error(pw.toString());
        } catch (ExceptionInInitializerError e1) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e1.printStackTrace(pw);
            LOG.error(String.format("Exception initializing loading of properties file: %s", PROP_FILE_NAME),e1);
            LOG.error(pw.toString());
        } catch (IOException e2) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e2.printStackTrace(pw);
            LOG.error(String.format("Error loading version from properties file: %s", PROP_FILE_NAME),e2);
            LOG.error(pw.toString());
        } finally {
            APP_VERSION = appVersion;
            SERVICE_PORT = servicePort;
            USE_ALL_CORES = useAllCores;
            THREAD_POOL_SIZE = threadPoolSize;
        }
    }

    //    public ImagingClient(int port, int poolSize) throws IOException {
//        String hostName = "localhost";
//        int portNumber = 1859;
//        serverSocket = new ServerSocket(SERVICE_PORT);
//        servicePool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
//        clientSocket = new Socket(hostName, portNumber);
//        clientPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
//    }
//
//    @Override
//    public void run() { // run the service
//        LOG.debug("Starting ImagingClient thread.");
//        try {
//            for (;;) {
//                LOG.debug("Starting the ImagingThread for the server");
////                servicePool.execute(new ImagingThread(this.serverSocket.accept()));
//                clientPool.execute(new ImagingThread(this.clientSocket));
//            }
//        } catch (IOException ex) {
//            LOG.error("Caught an IOException: shutting down ImagingClient thread pool, then exiting");
//            // pool.shutdown();
//            this.shutdownAndAwaitTermination(servicePool);
//        }
//    }
//
//    /**
//     * shutdownAndAwaitTermination()
//     *
//     * The following method shuts down an ExecutorService in two phases, first by calling shutdown to reject incoming tasks,
//     * and then calling shutdownNow, if necessary, to cancel any lingering tasks:
//     */
//    public void shutdownAndAwaitTermination(ExecutorService pool) {
//        pool.shutdown(); // Disable new tasks from being submitted
//        try {
//            // Wait a while for existing tasks to terminate
//            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
//                pool.shutdownNow(); // Cancel currently executing tasks
//                // Wait a while for tasks to respond to being cancelled
//                if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
//                    System.err.println("Pool did not terminate");
//                }
//            }
//        } catch (InterruptedException ie) {
//            // (Re-)Cancel if current thread also interrupted
//            pool.shutdownNow();
//            // Preserve interrupt status
//            Thread.currentThread().interrupt();
//        }
//    }
//
//    /**
//     * main()
//     */
//    public static void main(String[] args) throws IOException {
//        LOG.info("--------------------------------------------------------------------------------");
//        LOG.info("Welcome to Distributed Imaging CLIENT");
//        LOG.info(String.format("Version = %s", APP_VERSION));
//        LOG.info(String.format("Pool Size = %d", THREAD_POOL_SIZE));
//        LOG.info(String.format("Service Port = %d", SERVICE_PORT));
//        LOG.info("--------------------------------------------------------------------------------");
//        LOG.info("Finished ImagingClient boot-up.");
//
//        LOG.info(String.format("Starting ImagingClient on port %s ...", SERVICE_PORT));
//
//        ImagingClient imageClient = new ImagingClient(SERVICE_PORT, THREAD_POOL_SIZE);
//        Thread imageClientThread = new Thread(imageClient, "ImagingClient");
//        imageClientThread.start();
//        LOG.info("Exiting gracefully ImagingClient.");
//    }
    public static void main(String[] args) {
        String hostName = "localhost";
        int portNumber = 1859;
        InetAddress clientAddress = null;
        int clientPort = 0;

        try (
                Socket kkSocket = new Socket(hostName, portNumber);
                InputStream is = kkSocket.getInputStream();
                OutputStream os = kkSocket.getOutputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                BufferedOutputStream bos = new BufferedOutputStream(os);
                DataInputStream dis = new DataInputStream(bis);
                DataOutputStream dos = new DataOutputStream(bos);
                final Scanner scanner = new Scanner(System.in);
        ) {
//            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
//            String fromServer = dis.readUTF(); // Server sends WELCOME first
//            if (fromServer.equals("WELCOME")) {
            String fromServer;
            String fromUser;
            clientAddress = kkSocket.getInetAddress();
            clientPort = kkSocket.getPort();

            String[] commands = {"id rob", "info", "get cooper.jpg", "bye"};

            for (String command: commands) {
                LOG.debug("Running command {}", command);
                // send user command to server
                dos.writeUTF(command);
                dos.flush();
                LOG.debug("Wrote command to server: {}", fromUser);
                fromServer = dis.readUTF();
                if (fromServer != null) {
                    LOG.info("Reading command from server {}", fromServer);
                    // LOG.info("Server: " + fromServer);
                    if (fromServer.equals("quit")) {
                        try {
                            kkSocket.close();
                        } catch (IOException e) {
                            String errorMessage = String.format("IOException connected to %s on port %d", clientAddress.toString(), clientPort);
                            Utils.handleException(LOG, e, errorMessage);
                        }
                        break;
                    } else if (fromServer.startsWith("RECEIVE")) {
                        String fileName = fromServer.substring(8);
                        LOG.info(fileName);
                        int bufferSize = kkSocket.getReceiveBufferSize();
                        Utils.receiveFile(fileName, dis, bufferSize);
                        LOG.info("Finished receiving file {} from server", fileName);
                    }
                }
            }
            LOG.info("Awaiting next command from user");

//            LOG.info("User, please enter your first command: ");
//            while (scanner.hasNextLine()) {
//                fromUser = scanner.nextLine();
//                if (fromUser != null) {
//                    LOG.info("User command = {}", fromUser);
//                    // send user command to server
//                    dos.writeUTF(fromUser);
//                    dos.flush();
//                    LOG.debug("Wrote command to server: {}", fromUser);
//                    fromServer = dis.readUTF();
//                    if (fromServer != null) {
//                        LOG.info("Reading command from server {}", fromServer);
//                        // LOG.info("Server: " + fromServer);
//                        if (fromServer.equals("quit")) {
//                            try {
//                                kkSocket.close();
//                            } catch (IOException e) {
//                                String errorMessage = String.format("IOException connected to %s on port %d", clientAddress.toString(), clientPort);
//                                Utils.handleException(LOG, e, errorMessage);
//                            }
//                            break;
//                        } else if (fromServer.startsWith("RECEIVE")) {
//                            String fileName = fromServer.substring(8);
//                            LOG.info(fileName);
//                            int bufferSize = kkSocket.getReceiveBufferSize();
//                            Utils.receiveFile(fileName, dis, bufferSize);
//                            LOG.info("Finished receiving file {} from server", fileName);
//                        }
//                    }
//                }
//                LOG.info("Awaiting next command from user");
        }
    } catch (UnknownHostException e) {
        String errorMessage = String.format("Don't know about host ", hostName);
        Utils.handleException(LOG, e, errorMessage);
    } catch (IllegalArgumentException e) {
        String errorMessage = String.format("IllegalArgumentException connected to %s on port %d", clientAddress.toString(), clientPort);
        Utils.handleException(LOG, e, errorMessage);
    } catch (IOException e) {
        String errorMessage = String.format("IOException connected to %s on port %d", clientAddress.toString(), clientPort);
        Utils.handleException(LOG, e, errorMessage);
    } catch (Exception e) {
        System.err.println("General exception caught " + e);
    }
}
}

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
        String command = "";
        String commandNotSupported = "Command not supported: %s";
        try {
            LOG.debug("Awaiting command from client {} ({}:{})", this.clientName,
                    this.imagingThread.clientAddress.toString(), this.imagingThread.clientPort);
            command = dis.readUTF();
            LOG.info("User command received: {}", command);
            while (command != null) {
                setUpMatchers(command);
                if (MATCHER_MAP.get("QUIT").find()) {
                    response = RESPONSE_MAP.get("QUIT");
                    LOG.debug(response);
                    dos.writeUTF(response);
                    return;
                } else if (MATCHER_MAP.get("ID").find()) {
                    setClientName(command.substring(3));
                    response = String.format(RESPONSE_MAP.get("ID"), this.clientName);
                    LOG.debug(response);
                    dos.writeUTF(response);
                    continue;
                } else if (MATCHER_MAP.get("INFO").find()) {
                    response = String.format(RESPONSE_MAP.get("INFO"),
                            this.imagingThread.clientAddress.toString(), this.imagingThread.clientPort);
                    LOG.debug(response);
                    dos.writeUTF(response);
                    continue;
                } else if (MATCHER_MAP.get("FIND").find()) {
                    fileName = command.substring(5);
                    response = String.format(RESPONSE_MAP.get("FIND"), fileName);
                    LOG.debug(response);
                    dos.writeUTF(response);
                    continue;
                } else if (MATCHER_MAP.get("GET").find()) {
                    fileName = command.substring(4);
                    response = String.format(RESPONSE_MAP.get("GET"), fileName, this.clientName,
                            this.imagingThread.clientAddress.toString(), this.imagingThread.clientPort);
                    LOG.debug(response);
                    dos.writeUTF(String.format("RECEIVE %s", fileName));
                    LOG.debug(String.format("Sending file %s to client ID %s (%s:%s)", fileName, this.clientName,
                            this.imagingThread.clientAddress.toString(), this.imagingThread.clientPort));
                    Utils.sendFile(fileName, this.dos);
                    LOG.debug(String.format("Sent file %s to client ID %s (%s:%s)", fileName, this.clientName,
                            this.imagingThread.clientAddress.toString(), this.imagingThread.clientPort));
                    continue;
                } else {
                    response = String.format(commandNotSupported, command);
                    LOG.error(response);
                    dos.writeUTF(response);
                    continue;
                }
            }
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
}

package edu.cooper.ece465.apps.imaging;

import edu.cooper.ece465.utils.Utils;

import java.io.*;
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
        String commandNotSupported = "Command not supported: %s";
        try {
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();
                LOG.debug("Connected user command: {}", command);
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
                    printWriter.println(String.format("RECEIVE %s", fileName));
                    printWriter.flush();
                    LOG.debug(String.format("Send file %s to client ID %s (%s:%s)",fileName, this.clientName,
                            this.imagingThread.clientAddress.toString(), this.imagingThread.clientPort));
                    Utils.sendFile(fileName, this.outputStream);
                    LOG.debug(String.format("Sent file %s to client ID %s (%s:%s)",fileName, this.clientName,
                            this.imagingThread.clientAddress.toString(), this.imagingThread.clientPort));
                } else {
                    String message = String.format(commandNotSupported, command);
                    LOG.error(message);
                    printWriter.println(message);
                }
                printWriter.flush();
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
}

package edu.cooper.ece465.apps.imaging;

import edu.cooper.ece465.utils.Utils;

import java.io.*;
import java.net.*;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import veddan.physicalcores.PhysicalCores;

public class ImagingClient {
    private static final Logger LOG = LoggerFactory.getLogger(ImagingClient.class);
    public static final String PROP_FILE_NAME = "imaging.properties";
    public static final String APP_VERSION;
    public static final String SERVER_HOST;
    public static final int SERVICE_PORT;

    static {
        Properties props = new Properties();
        String appVersion = "app.version";
        String serverHost = "server.host";
        int servicePort = 1859;
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream(PROP_FILE_NAME);
            props.load(is);
            Utils.printProperties(props, LOG);
            appVersion = props.getProperty("app.version");
            serverHost = props.getProperty("server.host");
            servicePort = Integer.parseInt(props.getProperty("service.port"));
        } catch (NullPointerException e0) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e0.printStackTrace(pw);
            LOG.error(String.format("Null pointer exception initializing loading of properties file: %s", PROP_FILE_NAME),e0);
            LOG.error(pw.toString());
        } catch (ExceptionInInitializerError e1) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e1.printStackTrace(pw);
            LOG.error(String.format("Exception initializing loading of properties file: %s", PROP_FILE_NAME),e1);
            LOG.error(pw.toString());
        } catch (IOException e2) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e2.printStackTrace(pw);
            LOG.error(String.format("Error loading version from properties file: %s", PROP_FILE_NAME),e2);
            LOG.error(pw.toString());
        } finally {
            APP_VERSION = appVersion;
            SERVICE_PORT = servicePort;
            SERVER_HOST = serverHost;
        }
    }


    public static void main(String[] args) {
        String hostName = SERVER_HOST;
        int portNumber = SERVICE_PORT;
        InetAddress clientAddress = null;
        int clientPort = 0;

        try (
                Socket kkSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
                InputStream is = kkSocket.getInputStream();
                BufferedReader in = new BufferedReader( new InputStreamReader(is));
                DataInputStream dataInputStream = new DataInputStream(is);
        ) {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;
            clientAddress = kkSocket.getInetAddress();
            clientPort = kkSocket.getPort();

            while ((fromServer = in.readLine()) != null) {
                LOG.info("Reading command from server {}", fromServer);
//                LOG.info("Server: " + fromServer);
                if (fromServer.equals("Bye.")) {
                    try {
                        kkSocket.close();
                    } catch (IOException e) {
                        String errorMessage = String.format("IOException connected to %s on port %d", clientAddress.toString(), clientPort);
                        Utils.handleException(LOG, e, errorMessage);
                    }
                    break;
                } else if (fromServer.startsWith("RECEIVE")) {
                    String fileName = fromServer.substring(8);
                    LOG.info(fileName);
                    int bufferSize = kkSocket.getReceiveBufferSize();
                    Utils.receiveFile(fileName, is, bufferSize);
                    LOG.info("Finished receiving file {} from server", fileName);
                }

                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    LOG.info("Client: " + fromUser);
                    out.println(fromUser);
                }
            }
        } catch (UnknownHostException e) {
            String errorMessage = String.format("Don't know about host ", hostName);
            Utils.handleException(LOG, e, errorMessage);
        } catch (IllegalArgumentException e) {
            String errorMessage = String.format("IllegalArgumentException connected to %s on port %d", clientAddress.toString(), clientPort);
            Utils.handleException(LOG, e, errorMessage);
        } catch (IOException e) {
            String errorMessage = String.format("IOException connected to %s on port %d", clientAddress.toString(), clientPort);
            Utils.handleException(LOG, e, errorMessage);
        } catch (Exception e) {
            System.err.println("General exception caught " + e);
        }
    }
}

	public static void sendFile(String path, DataOutputStream dataOutputStream) throws Exception {
	    int bytes = 0;
	    File file = new File(path);
		byte[] fileContent = FileUtils.readFileToByteArray(file);
		String encodedString = Base64.getEncoder().encodeToString(fileContent);
		FileInputStream fileInputStream = new FileInputStream(file);

	    // send file size
	    dataOutputStream.writeLong(file.length());  
	    // break file into chunks
	    byte[] buffer = new byte[4*1024];
	    while ((bytes=fileInputStream.read(buffer))!=-1) {
	        dataOutputStream.write(buffer,0,bytes);
	        dataOutputStream.flush();
	    }
	    fileInputStream.close();
	}

	public static void receiveFile(String fileName, DataInputStream dis) throws Exception {
	    int bytes = 0;
	    FileOutputStream fos = new FileOutputStream(fileName);
	    
	    long size = dis.readLong();     // read file size
	    byte[] buffer = new byte[4*1024];
	    while (size > 0 && (bytes = dis.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
	        fos.write(buffer,0,bytes);
	        size -= bytes;      // read upto file size
	    }
	    fos.close();
	}

	public static void sendFile(String path, OutputStream os) throws Exception {
		DataOutputStream dos = new DataOutputStream(os);
		File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		DataInputStream dis = new DataInputStream(bis);

		int read;
		byte[] byteArray = new byte[8*1024];
//		dos.writeUTF(file.getName());
//		dos.writeLong(byteArray.length);

		while((read = dis.read(byteArray)) != -1) {
			dos.write(byteArray, 0, read);
		}
		dos.flush();
		dos.close();

//		byte[] fileBytes = IOUtils.toByteArray(fis);
//		String encodedString = Base64.getEncoder().encodeToString(fileBytes);
//		char[] charArray = encodedString.toCharArray();
////		byte[] byteArray = Base64.getEncoder().encodeToString(fileBytes);
////		char[] charArray = new String(byteArray).toCharArray();
//		System.out.println(String.format("Sending file %s of length %s: %s", path, charArray.length, encodedString));
//		out.write(charArray,0,charArray.length);
//		out.flush();

//		File file = new File(path);
//		byte[] fileContent = FileUtils.readFileToByteArray(file);
//		FileInputStream fileInputStream = new FileInputStream(file);
//		byte fileData[] = new byte[(int) file.length()];
//		fileInputStream.read(fileData);
//		String encodedString = Base64.getEncoder().encodeToString(fileContent);
//		String encodedString = IOUtils.toString(fileInputStream);
//		byte[] byteArray = Charset.forName("UTF-8").encode(CharBuffer.wrap(charArray)).array();
//		byte encodedByteArray[] = encodedString.getBytes(StandardCharsets.UTF_8);

	}

	public static void receiveFile(String path, Socket socket) throws Exception {
		int bufferSize=0;
		bufferSize=socket.getReceiveBufferSize();
		InputStream in = socket.getInputStream();
		DataInputStream dis = new DataInputStream(in);
//		String fileName = dis.readUTF();
//		System.out.println(fileName);
		OutputStream output = new FileOutputStream(path);
		byte[] buffer = new byte[bufferSize];
		int read;
		while((read = dis.read(buffer)) != -1){
			output.write(buffer, 0, read);
		}

////		String encodedString = IOUtils.toString(in);
//		//convert from base64 to byte array
////		byte[] fileByteArray = Base64.getDecoder().decode(encodedString);
//		//convert byte array to a file image
//		FileOutputStream fos = new FileOutputStream(path);
//		BufferedOutputStream bos = new BufferedOutputStream(fos);
//
//		char[] charArray = new char[4 * 1024];
//		int readLength = -1;
//
//		while ((readLength = in.read(charArray)) > 0) {
//			byte[] byteArray = Base64.getDecoder().decode(toBytes(charArray));
//			System.out.println(byteArray.toString());
//			bos.write(byteArray, 0, readLength);
//		}
//		bos.flush();
//		bos.close();
//		fos.close();
	}

	public static byte[] toBytes(char[] chars) {
		CharBuffer charBuffer = CharBuffer.wrap(chars);
		ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
		byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
				byteBuffer.position(), byteBuffer.limit());
		Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
		return bytes;
	}



```