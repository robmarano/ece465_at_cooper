package edu.cooper.ece465;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class distrComputingJourney {

	private static final Logger LOG = LoggerFactory.getLogger(distrComputingJourney.class);

	public static void main(String[] args) {
		System.out.println("starting distrComputingJourney.main()");
		LOG.debug("distrComputingJourney.main() - begin");

		Runner myThread = new Runner();
		System.out.println(myThread);
		myThread.start();

		LOG.debug("distrComputingJourney.main() - end");
		System.out.println("ending distrComputingJourney.main()");
	}
}