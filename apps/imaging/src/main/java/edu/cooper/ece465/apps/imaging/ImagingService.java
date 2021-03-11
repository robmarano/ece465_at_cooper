package edu.cooper.ece465.apps.imaging;

import edu.cooper.ece465.utils.Utils;

import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import veddan.physicalcores.PhysicalCores;

public class ImagingService implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(ImagingService.class);
	public static final String PROP_FILE_NAME = "imaging.properties";
	public static final String APP_VERSION;
	public static final boolean USE_ALL_CORES;
	public static final int THREAD_POOL_SIZE;
	public static final int SERVICE_PORT;

	private final ServerSocket serverSocket;
	private final ExecutorService pool;

	static {
		Properties props = new Properties();
		String appVersion = "app.version";
		boolean useAllCores = false;
		int threadPoolSize = 2;
		int servicePort = 1859;
		try {
			InputStream is = ClassLoader.getSystemResourceAsStream(PROP_FILE_NAME);
			props.load(is);
			Utils.printProperties(props, LOG);
			appVersion = props.getProperty("app.version");
			servicePort = Integer.parseInt(props.getProperty("service.port"));
			useAllCores = Boolean.getBoolean("cores.all");
			int numCores = PhysicalCores.physicalCoreCount().intValue();
			int poolSize = Integer.parseInt(props.getProperty("pool.size"));
			threadPoolSize = useAllCores ? numCores : poolSize;
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
			USE_ALL_CORES = useAllCores;
			THREAD_POOL_SIZE = threadPoolSize;
		}
	}

	public ImagingService(int port, int poolSize) throws IOException {
		serverSocket = new ServerSocket(SERVICE_PORT);
		pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	}

	@Override
	public void run() { // run the service
		LOG.debug("Starting ImagingService thread.");
		try {
			for (;;) {
				LOG.debug("Starting the ImagingThread for the server");
				pool.execute(new ImagingThread(serverSocket.accept()));
			}
		} catch (IOException ex) {
			LOG.error("Caught an IOException: shutting down ImagingService thread pool, then exiting");
			// pool.shutdown();
			this.shutdownAndAwaitTermination(pool);
		}
	}

	/**
	 * shutdownAndAwaitTermination()
	 *
	 * The following method shuts down an ExecutorService in two phases, first by calling shutdown to reject incoming tasks,
	 * and then calling shutdownNow, if necessary, to cancel any lingering tasks:
	 */
	public void shutdownAndAwaitTermination(ExecutorService pool) {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
		    // Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
		      pool.shutdownNow(); // Cancel currently executing tasks
		      // Wait a while for tasks to respond to being cancelled
		      if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
		      	System.err.println("Pool did not terminate");
		      }
		  }
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * main()
	 */
	public static void main(String[] args) throws IOException {
		LOG.info("--------------------------------------------------------------------------------");
		LOG.info("Welcome to Distributed Imaging Service");
		LOG.info(String.format("Version = %s", APP_VERSION));
		LOG.info(String.format("Pool Size = %d", THREAD_POOL_SIZE));
		LOG.info(String.format("Service Port = %d", SERVICE_PORT));
		LOG.info("--------------------------------------------------------------------------------");
		LOG.info("Finished ImagingService boot-up.");

		LOG.info(String.format("Starting ImagingService on port %s ...", SERVICE_PORT));

		ImagingService imageService = new ImagingService(SERVICE_PORT, THREAD_POOL_SIZE);
		Thread imageServiceThread = new Thread(imageService, "ImagingService");
		imageServiceThread.start();
		LOG.info("Exiting gracefully ImagingService.");
	}
}