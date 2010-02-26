// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis.tasks;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * BackgroundJob is the super-class of all low-priority background jobs that
 * Tablet runs (mainly DisplayData related calculations). See also TaskManager.
 */
public abstract class BackgroundTask implements Runnable
{
	// A listener to be notified when this task is completed
	protected ITaskListener listener;

	private AtomicInteger tCounter = new AtomicInteger();

	// A reference to (a possible) previous instance of this job
	protected BackgroundTask previous;

	protected boolean okToRun = true;
	protected boolean isRunning = true;

	public void addTaskListener(ITaskListener listener)
		{ this.listener = listener; }

	protected void notifyAndFinish()
	{
		if (listener != null && okToRun)
			listener.taskCompleted(new EventObject(this));

		isRunning = false;

		tCounter.decrementAndGet();
	}

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

	public void setPrevious(BackgroundTask previous)
		{ this.previous = previous; }

	public boolean isRunning()
		{ return isRunning; }
}