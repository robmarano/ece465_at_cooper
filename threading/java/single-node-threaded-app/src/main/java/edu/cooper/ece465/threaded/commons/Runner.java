package edu.cooper.ece465.threaded.commons;

import org.apache.log4j.Logger;

public class Runner extends Thread {

	private static final Logger LOG = Logger.getLogger(Runner.class);

	protected String toStringVal;

	public Runner() {
		toStringVal = "Runner class: " + super.toString();
	}

	@Override
	public void run() {
		LOG.debug("Runner.run() - begin");

		for (int i = 0; i < 100; i++) {
			LOG.info(this.toString() + ": " + i + " ");
		}

		LOG.debug("Runner.run() - end");
	}

	@Override
	public String toString(){
		return toStringVal;
	}
}
