package edu.cooper.ece465.concurrent.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
 
public class SimpleThreadPool {
	protected static final int THREAD_POOL_SIZE = 5;
 
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        for (int i = 0; i < 10; i++) {
            Runnable worker = new WorkerThread("" + i);
            executor.execute(worker);
          }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        System.out.println("Finished all threads");

        /*
        * we are creating fixed size thread pool of 5 worker threads.
        * Then we are submitting 10 jobs to this pool, since the pool size is 5,
        * it will start working on 5 jobs and other jobs will be in wait state,
        * as soon as one of the job is finished, another job from the wait queue
        * will be picked up by worker thread and get’s executed.
        */
    }
 
}