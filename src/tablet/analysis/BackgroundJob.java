// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * BackgroundJob is the super-class of all low-priority background jobs that
 * Tablet runs (mainly DisplayData related calculations). See also TaskManager.
 */
public abstract class BackgroundJob implements Callable<Boolean>
{
	protected AtomicInteger tCounter = new AtomicInteger();

	// A reference to (a possible) previous instance of this job
	protected BackgroundJob previous;

	protected boolean okToRun = true;
	protected boolean isRunning = true;

	/**
	 * Gives the (system-wide) thread counter to this job, which also increments
	 * its value by one, as this job will now be ready to run.
	 */
	public void setThreadCounter(AtomicInteger tCounter)
	{
		this.tCounter = tCounter;
		this.tCounter.incrementAndGet();
	}

	public void cancel()
		{ okToRun = false; }

	public void setPrevious(BackgroundJob previous)
		{ this.previous = previous; }

	protected void cleanup()
	{
		this.tCounter.decrementAndGet();
		isRunning = false;
	}
}