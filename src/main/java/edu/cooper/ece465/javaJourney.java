package edu.cooper.ece465;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class javaJourney {

	private static final Logger LOG = LoggerFactory.getLogger(javaJourney.class);

	public static void main(String[] args) {
		System.out.println("starting javaJourney.main()");
		LOG.debug("javaJourney.main() - begin");

		Runner myThread = new Runner();
		System.out.println(myThread);
		myThread.start();

		LOG.debug("javaJourney.main() - end");
		System.out.println("ending javaJourney.main()");
	}
}