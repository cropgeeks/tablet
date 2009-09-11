// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis;

import tablet.gui.*;

/**
 * Abstract base class that can be used for all "simple" trackable jobs (jobs
 * with just a single task to run).
 */
public abstract class SimpleJob implements ITrackableJob
{
	// Maximum progress bar value that we're aiming for
	protected int maximum = 0;
	// Current value;
	protected int progress = 0;

	protected boolean okToRun = true;

	public void cancelJob()
		{ okToRun = false; }

	public boolean isIndeterminate()
		{ return maximum == 0; }

	public int getMaximum()
		{ return maximum; }

	public int getValue()
		{ return progress; }

	public int getJobCount()
		{ return 1; }
}