package tablet.io;

import tablet.data.*;
import tablet.data.cache.*;
import tablet.gui.*;

import scri.commons.file.*;

abstract class AssemblyReader implements ITrackableJob
{
	// Data structures used as the file is read
	protected Assembly assembly = new Assembly();
	protected IReadCache readCache;

	// True while this trackable job should still be running
	protected boolean okToRead = true;

	// The input stream being read from
	protected ProgressInputStream is;

	void setParameters(IReadCache readCache, ProgressInputStream is)
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
		{ return 50000; }

	public int getValue()
	{
		float bytesRead = is.getBytesRead();
		float size = is.getSize();

		return Math.round((bytesRead / size) * 50000);
	}

	public int getJobCount()
		{ return 1; }
}