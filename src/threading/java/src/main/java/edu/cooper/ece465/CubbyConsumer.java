package edu.cooper.ece465;

public class CubbyConsumer extends Thread {
    private CubbyHole cubbyhole;
    private int number;

    public CubbyConsumer(CubbyHole c, int number) {
        cubbyhole = c;
        this.number = number;
    }
 
    public void run() {
        int value = 0;
        for (int i = 0; i < ProducerConsumerTest.CONSUME_SIZE; i++) {
            value = cubbyhole.get();
            System.out.println("Consumer #" + this.number + " got: " + value);
        }
    }
}
