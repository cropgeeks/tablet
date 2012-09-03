// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.analysis.tasks;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * TaskManager is a class responsible for maintaining a pool of background
 * worker threads, that are used for various calculations by other Tablet
 * components (mainly DisplayData related objects). The analysis classes that
 * do the work must be submitted for execution via TaskManager.
 *
 * The class "remembers" (in a hashtable) each of the previous jobs, with every
 * job type being identified by a job-type-string (eg, "BaseMappingCalculator"
 * or "ProteinTranslater:0:1". Only one type of job can be in the pool at a
 * time. If a new job of that type (which will have the same name) is submitted,
 * then the previous instance of it is cancelled.
 */
public class TaskManager
{
	private static ThreadPoolExecutor executor;

	private static ConcurrentHashMap<String, BackgroundTask> tasks;

	static
	{
		int cores = Runtime.getRuntime().availableProcessors();
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(cores);

		tasks = new ConcurrentHashMap<String, BackgroundTask>();
	}

	public static int count()
	{
		return (int)(executor.getTaskCount() - executor.getCompletedTaskCount());
	}

	public static void cancelAll()
	{
		Enumeration<String> keys = tasks.keys();

		while (keys.hasMoreElements())
		{
			String name = keys.nextElement();
			BackgroundTask task = tasks.get(name);

			task.cancel();
		}

		tasks.clear();
	}

	public static void cancel(String name)
	{
		BackgroundTask task = tasks.get(name);

		if (task != null)
			task.cancel();
	}

	public static void submit(String name, BackgroundTask task)
	{
		BackgroundTask previous = tasks.get(name);

		// If a previous instance exists...
		if (previous != null)
		{
			// Cancel it
			previous.cancel();

			// Tell the new instance about it (so it can wait on it finishing)
			task.setPrevious(previous);
		}

		tasks.put(name, task);

		executor.submit(task);
	}
}