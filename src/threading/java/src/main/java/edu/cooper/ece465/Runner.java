package edu.cooper.ece465;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Runner extends Thread {

	private Log LOG = LogFactory.getLog(Runner.class);
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
	public String toString(){
		return toStringVal;
	}
}
