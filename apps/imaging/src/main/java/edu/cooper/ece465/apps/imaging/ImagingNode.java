package edu.cooper.ece465.apps.imaging;

import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import edu.cooper.ece465.utils.Utils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import veddan.physicalcores.PhysicalCores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImagingNode implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ImagingNode.class);
    public static final String PROP_FILE_NAME = "imaging.properties";
    public static final int SERVER_SOCKET_TIMEOUT_MSEC = 60*1000;
    public static final int RETRY_NUM_LIMIT = 10;
    public static final String APP_VERSION;
    public static final boolean USE_ALL_CORES;
    public static final int THREAD_POOL_SIZE;
    public static final int SERVICE_PORT;

    private final ServerSocket serverSocket;
    private final Socket clientSocket;
    private final ExecutorService pool;
    private String peerHost;
    private int peerPort;
    private int serverPort;
    private String id;

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
            LOG.error(String.format("Null pointer exception initializing loading of properties file: %s", PROP_FILE_NAME), e0);
            LOG.error(pw.toString());
        } catch (ExceptionInInitializerError e1) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e1.printStackTrace(pw);
            LOG.error(String.format("Exception initializing loading of properties file: %s", PROP_FILE_NAME), e1);
            LOG.error(pw.toString());
        } catch (IOException e2) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e2.printStackTrace(pw);
            LOG.error(String.format("Error loading version from properties file: %s", PROP_FILE_NAME), e2);
            LOG.error(pw.toString());
        } finally {
            APP_VERSION = appVersion;
            SERVICE_PORT = servicePort;
            USE_ALL_CORES = useAllCores;
            THREAD_POOL_SIZE = threadPoolSize;
        }
    }

    public ImagingNode(String id, int serverPort, String peerHost, int peerPort) throws Exception {
        this.id = id;
        this.serverPort = serverPort;
        this.serverSocket = new ServerSocket(serverPort);
//        this.serverSocket.setSoTimeout(SERVER_SOCKET_TIMEOUT_MSEC);
        this.pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.peerHost = peerHost;
        this.peerPort = peerPort;
        this.clientSocket = this.connectToPeer(this.peerHost, this.peerPort);
        if (this.clientSocket == null) {
            LOG.error("Unable to connect to peer: {}:{}", this.peerHost, this.peerPort);
        }
        LOG.debug("{}, {}, {}, {}",this.id,this.serverPort,this.peerHost,this.peerPort);
    }

    private Socket connectToPeer(String peer, int port) {
        LOG.debug("ID {} attempting to connect to peer: {}:{}", this.id, peer, port);
        int tryNum = 1;
        int secToWait = 0;
        boolean connected = false;
        Socket cSocket = null;
        while ( (!connected) && (tryNum <= RETRY_NUM_LIMIT) ) {
            LOG.debug("tryNum={}, secToWait={}",tryNum,secToWait);
            try {
                cSocket = new Socket(peerHost, peerPort);
                connected = true;
            } catch (ConnectException ex) {
                String errorMessage = String.format("ConnectException: %s can't connect to peer: %s:%d",
                        id, peer, port);
//                Utils.handleException(LOG, ex, errorMessage);
                secToWait = (int) Math.round(Math.min(5, Math.pow(2, tryNum++)));
                try {
                    Thread.sleep(secToWait * 1000L);
                } catch (InterruptedException ex1) {
                    String errMsg = String.format("InterruptedException: %s backoff: tryNum=%d, wait=%s sec",
                            id, tryNum, secToWait);
                    Utils.handleException(LOG, ex1, errMsg);
                }
            } catch (UnknownHostException ex) {
                String errorMessage = String.format("UnknownHostException: %s can't connect to peer: %s:%d",
                        id, peer, port);
                Utils.handleException(LOG, ex, errorMessage);
                break;
            } catch (IOException ex) {
                String errorMessage = String.format("IOException: %s can't connect to peer: %s:%d",
                        id, peer, port);
                Utils.handleException(LOG, ex, errorMessage);
                break;
            }
        }
        LOG.info("{} connected to peer as client: {}:{}", id, peer, port);
        return(cSocket);
    }

    public void executeCommands(String path) {
        if (this.clientSocket != null) {
            BufferedReader reader;
            String line = null;
            try {
                reader = new BufferedReader(new FileReader(path));
                line = reader.readLine();
                while (line != null) {
                    this.executeCommand(line);
                    line = reader.readLine();
                }
                reader.close();
            } catch (IOException ex) {
                String errorMessage = String.format("IOException: on command %s", line);
                Utils.handleException(LOG, ex, errorMessage);
                Utils.shutdownAndAwaitTermination(pool);
            } catch (Exception ex) {
                String errorMessage = "Exception: shutting down ImagingService thread pool, then exiting";
                Utils.handleException(LOG, ex, errorMessage);
                Utils.shutdownAndAwaitTermination(pool);
            }
        } else {
            LOG.error("Unable to send commands to peer {}:{} due to no client connection",
                    this.peerHost, this.peerPort);
        }
    }

    public void executeCommand(String command) throws Exception{
        InputStream is = this.clientSocket.getInputStream();
        OutputStream os = this.clientSocket.getOutputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(os);
        DataInputStream dis = new DataInputStream(bis);
        DataOutputStream dos = new DataOutputStream(bos);

        LOG.info("User command = {}", command);
        // send user command to server
        dos.writeUTF(command);
        dos.flush();
        LOG.debug("Wrote command to server: {}", command);
        String fromServer = dis.readUTF();
        if (fromServer != null) {
            LOG.info("Reading command from server {}", fromServer);
            if (fromServer.startsWith("RECEIVE")) {
                String fileName = fromServer.substring(8);
                LOG.info(fileName);
                int bufferSize = this.clientSocket.getReceiveBufferSize();
                Utils.receiveFile(fileName, dis, bufferSize);
                LOG.info("Finished receiving file {} from server", fileName);
            }
        }
        LOG.info("Awaiting next command from user");
    }


    @Override
    public void run() { // run the service
        LOG.debug("Starting ImagingNode thread.");
        try {
            while (true) {
                LOG.debug("Starting the ImagingThread for ImagingNode");
                Socket connectedClient = this.serverSocket.accept();
                LOG.info("Client connected: {}", connectedClient.toString());
                ImagingThread imagingThread = new ImagingThread(connectedClient);
                LOG.debug("Created new imagingThread: {}", imagingThread);
                pool.execute(imagingThread);
            }
        } catch (ConnectException ex) {
            String errorMessage = "Found ConnectException";
            Utils.handleException(LOG, ex, errorMessage);
        } catch (InterruptedIOException ex) {
            String errorMessage = "Found InterruptedIOException";
            Utils.handleException(LOG, ex, errorMessage);
        } catch (IOException ex) {
            String errorMessage = "IOException: shutting down ImagingNode thread pool, then exiting";
            Utils.handleException(LOG, ex, errorMessage);
            Utils.shutdownAndAwaitTermination(pool);
        }
    }


    public static void main(String[] args) {
        ImagingNode nodeOne;
        int serverPort = 5000;
        String peerHost = "localhost";
        int peerPort = 5001;
        String id = "ImagingNode";
        String commandFile = "commands.dat";

        Options options = new Options();
        options.addOption(Option.builder("i")
                .longOpt("id")
                .hasArg(true)
                .desc("this service id ([REQUIRED] or use --id)")
                .required()
                .build());
        options.addOption(Option.builder("p")
                .longOpt("port")
                .hasArg(true)
                .desc("this service port number ([REQUIRED] or use --port)")
                .required()
                .build());
        options.addOption(Option.builder("h")
                .longOpt("peer")
                .hasArg(true)
                .desc("peer hostname or IP address ([REQUIRED] or use --peer)")
                .required()
                .build());
        options.addOption(Option.builder("P")
                .longOpt("peerport")
                .hasArg(true)
                .desc("peer host port number ([REQUIRED] or use --peerport")
                .required()
                .build());
        options.addOption(Option.builder("f")
                .longOpt("file")
                .hasArg(true)
                .desc("command file ([REQUIRED] or use --file")
                .required()
                .build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
//        LOG.debug("args = {} ; options = {}", args, options.toString());
        try {
            cmd = parser.parse(options, args);

            boolean hasId = cmd.hasOption("id");
            boolean hasPort = cmd.hasOption("port");
            boolean hasPeer = cmd.hasOption("peer");
            boolean hasPeerPort = cmd.hasOption("peerport");
            boolean hasCommandFile = cmd.hasOption("file");
            if (hasId && hasPort && hasPeer && hasPeerPort && hasCommandFile) {
                id = cmd.getOptionValue("id");
                LOG.debug("id = {}", id);
                serverPort = Integer.parseInt(cmd.getOptionValue("port"));
                LOG.debug("serverPort = {}", serverPort);
                peerHost = cmd.getOptionValue("peer");
                LOG.debug("peerHost = {}", peerHost);
                peerPort = Integer.parseInt(cmd.getOptionValue("peerport"));
                LOG.debug("peerPort = {}", peerPort);
                commandFile = cmd.getOptionValue("file");
                LOG.debug("commandFile = {}", commandFile);
            } else {
                System.out.println("please specify the command line options: "
                        + " -i,--id <arg>         this service id"
                        + "AND\n"
                        + " -p,--port <arg>       this service port number"
                        + "AND\n"
                        + " -h,--peer <arg>       peer hostname or IP"
                        + "AND\n"
                        + " -P,--peerport <arg>   peer host port number");
            }
        } catch (ParseException pe) {
            System.out.println("Error parsing command-line arguments!");
            System.out.println("Please, follow the instructions below:");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "ImagingNode", options );
            System.exit(1);
        }

//        LOG.info("--------------------------------------------------------------------------------");
//        LOG.info("Welcome to Distributed Imaging Service - ImagingNode");
//        LOG.info(String.format("Version = %s", APP_VERSION));
//        LOG.info(String.format("Pool Size = %d", THREAD_POOL_SIZE));
//        LOG.info(String.format("Service Port = %d", SERVICE_PORT));
//        LOG.info("--------------------------------------------------------------------------------");
//        LOG.info("Finished ImagingNode boot-up.");

        LOG.info("{}, {}, {}, {}",id, serverPort, peerHost, peerPort);

        try {
            nodeOne = new ImagingNode(id, serverPort, peerHost, peerPort);
            Thread nodeOneThread = new Thread(nodeOne, id);
            nodeOneThread.start();
            nodeOne.executeCommands(commandFile);
            LOG.info(String.format("Starting %s on port %d -- connected to %s:%d...",
                    id, serverPort, peerHost, peerPort));
        } catch (Exception e1) {
            String errorMessage = "Found exception";
            Utils.handleException(LOG, e1, errorMessage);
        } catch (Error err) {
            String errorMessage = "Found error";
            Utils.handleError(LOG, err, errorMessage);
        }
        LOG.info("Exiting gracefully {}.", id);
    }
}
