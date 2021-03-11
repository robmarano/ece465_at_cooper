package edu.cooper.ece465.apps.imaging;

import edu.cooper.ece465.utils.Utils;

import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.Scanner;

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
            InputStream is = kkSocket.getInputStream();
            OutputStream os = kkSocket.getOutputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            DataInputStream dis = new DataInputStream(bis);
            DataOutputStream dos = new DataOutputStream(bos);
            final Scanner scanner = new Scanner(System.in);
        ) {
//            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromServer = null;
            String fromUser;
            clientAddress = kkSocket.getInetAddress();
            clientPort = kkSocket.getPort();

            LOG.info("User, please enter your first command: ");
            while (scanner.hasNextLine()) {
                fromUser = scanner.nextLine();
                if (fromUser != null) {
                    fromServer = null;
                    LOG.info("User command = {}", fromUser);
                    // send user command to server
                    dos.writeUTF(fromUser);
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