package edu.cooper.ece465.single;

import edu.cooper.ece465.threaded.commons.Consumer;
import edu.cooper.ece465.threaded.commons.Producer;
import edu.cooper.ece465.threaded.commons.Drop;

import edu.cooper.ece465.commons.utils.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ProducerConsumerExample {
	private static final Logger LOG = Logger.getLogger(ProducerConsumerExample.class);

    public static int PROD_SIZE = 5;
    public static int CONSUME_SIZE = 5;

    static {
	    Properties prop = new Properties();
	    InputStream input = (FileInputStream) null;
	    try {
//	    	input = ClassLoader.getSystemClassLoader().getResourceAsStream("cubbyHole.properties");
//	    	input = ProducerConsumerTest.class.getResourceAsStream("/cubbyHole.properties");
	    	ClassLoader loader = ProducerConsumerTest.class.getClassLoader();
	    	InputStream in = loader.getResourceAsStream("cubbyHole.properties");
	    	// InputStream in = loader.getResourceAsStream("app.commons.properties");
	    	prop.load(in);
	    	Utils.printProperties(prop, LOG);
	    	System.out.println(prop.getProperty("app.version"));
	    	System.out.println(prop.getProperty("app.prod.size"));
	    	System.out.println(prop.getProperty("app.consume.size"));
	    	System.out.println(prop.getProperty("prod.size"));
	    	System.out.println(prop.getProperty("consume.size"));
	    	PROD_SIZE = Integer.parseInt(prop.getProperty("prod.size"));
	    	CONSUME_SIZE = Integer.parseInt(prop.getProperty("consume.size"));
		} catch (Exception ex1) {
			String errorMessage = String.format("Encountered a FATAL exception: %s", ex1.getMessage());
			Utils.handleException(LOG, ex1, errorMessage);
		} catch (Error ex2) {
			String errorMessage = String.format("Encountered a FATAL error: %s", ex2.getMessage());
			Utils.handleError(LOG, ex2, errorMessage);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


    public static void main(String[] args) {
        Drop drop = new Drop();
        (new Thread(new Producer(drop))).start();
        (new Thread(new Consumer(drop))).start();
        (new Thread(new Consumer(drop))).start();
    }
}
