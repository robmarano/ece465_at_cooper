package edu.cooper.ece465.single;

import edu.cooper.ece465.threaded.CubbyHole;
import edu.cooper.ece465.threaded.CubbyProducer;
import edu.cooper.ece465.threaded.CubbyConsumer;

import org.apache.log4j.Logger;

public class ProducerConsumerTest {
    public static final int PROD_SIZE = 1000;
    public static final int CONSUME_SIZE = 1000;

    public static void main(String[] args) {
        CubbyHole c = new CubbyHole();
        CubbyProducer p1 = new CubbyProducer(c, 1);
        CubbyProducer p2 = new CubbyProducer(c, 2);
        CubbyConsumer c1 = new CubbyConsumer(c, 1);
        CubbyConsumer c2 = new CubbyConsumer(c, 2);
 
        p1.start();
        c1.start();
        p2.start();
        c2.start();
    }
}
