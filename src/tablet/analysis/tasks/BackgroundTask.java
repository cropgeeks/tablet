// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.analysis.tasks;

import java.util.*;
import java.util.concurrent.*;

/**
 * BackgroundJob is the super-class of all low-priority background jobs that
 * Tablet runs (mainly DisplayData related calculations). See also TaskManager.
 */
public abstract class BackgroundTask implements Runnable
{
	// A listener to be notified when this task is completed
	protected ITaskListener listener;

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

		if (okToRun == false)
			doCleanup();

		isRunning = false;
	}

	public void cancel()
		{ okToRun = false; }

	public void setPrevious(BackgroundTask previous)
		{ this.previous = previous; }

	public boolean isRunning()
		{ return isRunning; }

	/** Cleanup operations that only happen if the job was cancelled. */
	protected abstract void doCleanup();
}