package edu.cooper.ece465.concurrent;

import java.util.Date;
import java.util.Timer;	
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Tasks can be scheduled for execution in the future. Such tasks can be
 * performed either periodically, or just once. As well, the first execution
 * can be delayed to a specific time in the future. There are two different
 * styles of implementing a scheduled task:
 * (a) ScheduledExecutorService and ScheduledFuture, and
 * (b) Timer and TimerTask
 * The first pair is the more modern API. As usual,
 * the more modern API is usually the preferred one. The main difference
 * between these two APIs is that the first always uses relative times,
 * while the second does not. If needed, you always can transform a Date
 * into a relative time:
 * Date futureDate = ...
 * long startTime = futureDate.getTime() - System.currentTimeMillis();
 * (This will only work if the system clock is not reset.)
 */
 
public class MyTimerTask extends TimerTask {

	private static final Logger LOG = LoggerFactory.getLogger(MyTimerTask.class);
 
    @Override
    public void run() {
    	Date date = new Date();
        System.out.println("Timer task started at:"+date);
        LOG.info("Timer task started at:"+date);
        completeTask();
        date = new Date();
        System.out.println("Timer task finished at:"+date);
        LOG.info("Timer task started at:"+date);
    }
 
    private void completeTask() {
        try {
            //assuming it takes 20 secs to complete the task
        	System.out.println("completeTask() - sleeping");
        	LOG.info("completeTask() - sleeping");
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
     
    public static void main(String args[]){
        TimerTask timerTask = new MyTimerTask();
        //running timer task as daemon thread
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 10*1000);
        System.out.println("TimerTask started");
        LOG.info("TimerTask started");
        //cancel after sometime
        try {
            Thread.sleep(120000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        timer.cancel();
        System.out.println("TimerTask cancelled");
        LOG.info("TimerTask cancelled");
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
 
}