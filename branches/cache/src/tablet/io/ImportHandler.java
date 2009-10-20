// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.cache.*;
import tablet.gui.*;

import scri.commons.file.*;
import scri.commons.gui.*;

/**
 * ImportHandler is a 2-state ITrackableJob implementation, that is responsible
 * for monitoring progress during assembly import and read base comparisons.
 */
public class ImportHandler implements ITrackableJob
{
	private File[] files;
	private boolean okToRun = true;

	private ITrackableJob currentJob = null;

	private Assembly assembly;

	public ImportHandler(String[] filenames)
	{
		files = new File[filenames.length];
		for (int i = 0; i < files.length; i++)
			files[i] = new File(filenames[i]);
	}

	// Decide which part of the import operation to run; then run it
	public void runJob(int jobIndex)
		throws Exception
	{
		if (okToRun == false)
			return;

		// Import the assembly file
		if (jobIndex == 0)
			currentJob = new AssemblyFileHandler(files);

		currentJob.runJob(0);

		assembly = ((AssemblyFileHandler)currentJob).getAssembly();
	}

	public Assembly getAssembly()
		{ return assembly; }

	public int getJobCount()
		{ return 1; }

	public void cancelJob()
	{
		okToRun = false;

		if (currentJob != null)
			currentJob.cancelJob();
	}

	public int getValue()
	{
		if (currentJob != null)
			return currentJob.getValue();

		return 0;
	}

	public int getMaximum()
	{
		if (currentJob != null)
			return currentJob.getMaximum();

		return 0;
	}

	public boolean isIndeterminate()
	{
		if (currentJob != null)
			return currentJob.isIndeterminate();

		return false;
	}
}