package edu.cooper.ece465.apps.imaging;

import java.net.*;
import java.io.*;
 
public class ImagingThread extends Thread {
    private Socket socket = null;
 
    public ImagingThread(Socket socket) {
        super("ImagingThread");
        this.socket = socket;
    }
     
    public void run() {
 
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()));
        ) {
            String inputLine, outputLine;
            ImagingProtocol dip = new ImagingProtocol();
            outputLine = dip.processInput(null);
            out.println(outputLine);
 
            while ((inputLine = in.readLine()) != null) {
                outputLine = dip.processInput(inputLine);
                out.println(outputLine);
                if (outputLine.equals("Bye"))
                    break;
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}