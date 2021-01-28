package edu.cooper.ece465.threaded;

public class CubbyConsumer extends Thread {

    public static int ITER_SIZE = 1024;

    private CubbyHole cubbyhole;
    private int number;

    public CubbyConsumer(CubbyHole c, int number) {
        cubbyhole = c;
        this.number = number;
    }
 
    public void run() {
        int value = 0;
        for (int i = 0; i < CubbyConsumer.ITER_SIZE; i++) {
            value = cubbyhole.get();
            System.out.println("Consumer #" + this.number + " got: " + value);
        }
    }
}
