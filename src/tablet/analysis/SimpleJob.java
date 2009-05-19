package tablet.analysis;

import tablet.gui.*;

abstract class SimpleJob implements ITrackableJob
{
	// Maximum progress bar value that we're aiming for
	protected int maximum = -1;
	// Current value;
	protected int progress = 0;

	protected boolean okToRun = true;

	public void cancelJob()
		{ okToRun = false; }

	public boolean isIndeterminate()
		{ return maximum == -1; }

	public int getMaximum()
		{ return maximum; }

	public int getValue()
		{ return progress; }

	public int getJobCount()
		{ return 1; }
}