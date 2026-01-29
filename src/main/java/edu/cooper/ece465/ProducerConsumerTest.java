package edu.cooper.ece465;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ProducerConsumerTest {
    public static int PROD_SIZE;
    public static int CONSUME_SIZE;

    static {
	    Properties prop = new Properties();
	    InputStream input = (FileInputStream) null;
	    try {
//	    	input = ClassLoader.getSystemClassLoader().getResourceAsStream("cubbyHole.properties");
//	    	input = ProducerConsumerTest.class.getResourceAsStream("/cubbyHole.properties");
	    	ClassLoader loader = ProducerConsumerTest.class.getClassLoader();
	    	InputStream in = loader.getResourceAsStream("cubbyHole.properties");
	    	prop.load(in);
	    	System.out.println(prop.getProperty("prod.size"));
	    	PROD_SIZE = Integer.parseInt(prop.getProperty("prod.size"));
	    	CONSUME_SIZE = Integer.parseInt(prop.getProperty("consume.size"));
		} catch (Exception ex) {
			ex.printStackTrace();
//			PROD_SIZE = 5;
//			CONSUME_SIZE = 5;
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
        CubbyHole c = new CubbyHole();
        CubbyProducer p1 = new CubbyProducer(c, 1);
        CubbyProducer p2 = new CubbyProducer(c, 2);
        CubbyConsumer c1 = new CubbyConsumer(c, 1);
        CubbyConsumer c2 = new CubbyConsumer(c, 2);
 
        c1.start();
        c2.start();
        p1.start();
        p2.start();
    }
}