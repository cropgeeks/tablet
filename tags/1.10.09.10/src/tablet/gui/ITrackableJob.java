// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui;

/**
 * Interface for tasks that are likely to be run in a threaded environment,
 * and can provide progress-bar feedback to a GUI as they run.
 */
public interface ITrackableJob
{
	/** Returns the number of sub jobs that this trackable job will run. */
	int getJobCount();

	/** Runs the job with index jobIndex (counting from 0). */
	void runJob(int jobIndex) throws Exception;

	/** Returns true if this job cannot currently be progress bar tracked. */
	boolean isIndeterminate();

	/** Returns the current maximum "value" that the job is trying to reach. */
	int getMaximum();

	/** Returns the current value for progress through the job. */
	int getValue();

	/** Indicates to the job that it should cancel whatever it is doing. */
	void cancelJob();

	/** An optional (null if not used) message to display while running. */
	String getMessage();
}