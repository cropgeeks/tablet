package tablet.io;

import java.io.*;

import tablet.data.*;
import tablet.data.cache.*;
import tablet.gui.*;

abstract class AssemblyReader implements ITrackableJob
{
	// Data structures used as the file is read
	protected Assembly assembly = new Assembly();
	protected IReadCache readCache;

	// True while this trackable job should still be running
	protected boolean okToRead = true;

	// Maximum progress bar value that we're aiming for
	protected int maximum = 100;
	// Current value;
	protected int progress = 0;

	// The input stream being read from
	protected InputStream is;

	void setParameters(IReadCache readCache, InputStream is)
	{
		this.readCache = readCache;
		this.is = is;
	}

	public void cancelJob()
		{ okToRead = false; }

	Assembly getAssembly()
		{ return assembly; }

	public boolean isIndeterminate()
		{ return false; }

	public int getMaximum()
		{ return maximum; }

	public int getValue()
		{ return progress; }

	public int getJobCount()
		{ return 1; }
}