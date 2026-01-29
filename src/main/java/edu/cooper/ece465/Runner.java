package edu.cooper.ece465;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Runner extends Thread {

	private final Logger LOG = LoggerFactory.getLogger(Runner.class);
	protected String toStringVal;

	public Runner() {
		toStringVal = "Runner class: " + super.toString();
	}

	@Override
	public void run() {
		LOG.debug("Runner.run() - begin");

		for (int i = 0; i < 100; i++) {
			System.out.print(" " + i + " ");
		}

		LOG.debug("Runner.run() - end");
	}

	@Override
	public String toString() {
		return toStringVal;
	}
}