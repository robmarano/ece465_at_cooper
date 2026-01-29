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

            String[] commands = {
                    "id rob",
                    "info",
                    "get test.file",
                    "get cooper.jpg",
                    "quit"
            };

            for (String command: commands) {
                LOG.debug("Running command {}", command);
                // send user command to server
                dos.writeUTF(command);
                dos.flush();
                LOG.debug("Wrote command to server: {}", command);
                fromServer = dis.readUTF();
                if (fromServer != null) {
                    LOG.info("Reading command from server {}", fromServer);
                    // LOG.info("Server: " + fromServer);
                    if (fromServer.equals("BYE")) {
//                        try {
//                            kkSocket.close();
//                        } catch (IOException e) {
//                            String errorMessage = String.format("IOException connected to %s on port %d", clientAddress.toString(), clientPort);
//                            Utils.handleException(LOG, e, errorMessage);
//                        }
                        break;
                    } else if (fromServer.startsWith("RECEIVE")) {
                        String fileName = fromServer.substring(8);
                        LOG.info(fileName);
                        int bufferSize = kkSocket.getReceiveBufferSize();
                        Utils.receiveFile(fileName, dis, bufferSize);
                        LOG.info("Finished receiving file {} from server", fileName);
                    }
                }
                LOG.info("Awaiting next command from user");
                Thread.sleep(2000);
            }

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
//            }
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