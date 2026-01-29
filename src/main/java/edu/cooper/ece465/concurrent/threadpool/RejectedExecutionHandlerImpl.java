package edu.cooper.ece465.concurrent.threadpool;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/*
 * Executors class provide simple implementation of ExecutorService using
 * ThreadPoolExecutor but ThreadPoolExecutor provides much more feature than
 * that. We can specify the number of threads that will be alive when we create
 * ThreadPoolExecutor instance and we can limit the size of thread pool and create
 * our own RejectedExecutionHandler implementation to handle the jobs that can’t fit
 * in the worker queue.
 */

public class RejectedExecutionHandlerImpl implements RejectedExecutionHandler {
 
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        System.out.println(r.toString() + " is rejected");
    }
 
}