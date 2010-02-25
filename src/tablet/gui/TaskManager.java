// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import tablet.analysis.*;

/**
 * TaskManager is a class responsible for maintaining a pool of background
 * worker threads, that are used for various calculations by other Tablet
 * components (mainly DisplayData related objects). The analysis classes that
 * do the work must be submitted for execution via TaskManager.
 */
public class TaskManager
{
	private static ExecutorService executor;
	private static AtomicInteger tCounter;

	// What tasks can be run. We want to track the last instance of each type
	private static BaseMappingCalculator baseMappingCalculator;

	static
	{
		int cores = Runtime.getRuntime().availableProcessors();

		executor = Executors.newFixedThreadPool(cores);
		tCounter = new AtomicInteger(0);
	}

	public static AtomicInteger getThreadCounter()
		{ return tCounter; }

	public static void cancelAll()
	{
		if (baseMappingCalculator != null)
			baseMappingCalculator.cancel();
	}

	public static void submit(BaseMappingCalculator job)
	{
		// If a previous instance exists...
		if (baseMappingCalculator != null)
		{
			// Cancel it
			baseMappingCalculator.cancel();

			// Tell the new instance about it (so it can wait on it finishing)
			job.setPrevious(baseMappingCalculator);
		}

		baseMappingCalculator = job;
		job.setThreadCounter(tCounter);

		executor.submit(job);
	}
}