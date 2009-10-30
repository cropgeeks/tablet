// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;

import tablet.analysis.*;
import tablet.data.*;

import scri.commons.file.*;

/**
 * Tracks reading from one or more files. Tracking is based on all the files
 * together so will go from 0 to (total size of all files).
 */
abstract class TrackableReader extends SimpleJob
{
	// Read data
	protected File[] files;
	protected ProgressInputStream is;
	protected BufferedReader in;
	protected String str;
	protected int lineCount;

	protected Assembly assembly;

	// Tracks how many bytes have been read from each of the files
	private long[] bytesRead;
	// Tracks the current file being read from
	private int fileIndex;

	// The total size (in bytes) of all the files
	private long totalSize;

	void setInputs(File[] files, Assembly assembly)
	{
		this.files = files;
		this.assembly = assembly;

		bytesRead = new long[files.length];

		for (File file: files)
			totalSize += file.length();
	}

	String readLine()
		throws IOException
	{
		lineCount++;
		return in.readLine();
	}

	Assembly getAssembly()
		{ return assembly; }

	public boolean isIndeterminate()
		{ return false; }

	public int getMaximum()
		{ return 5555; }

	public int getValue()
	{
		if (is == null)
			return 0;

		// Update the value for the file currently being read
		bytesRead[fileIndex] = is.getBytesRead();

		// But calculate the overall percentage using all the files
		long total = 0;
		for (long bytes: bytesRead)
			total += bytes;

		return Math.round((total / (float) totalSize) * 5555);
	}

	ProgressInputStream getInputStream(int fileIndex)
		throws Exception
	{
		this.fileIndex = fileIndex;

		is = new ProgressInputStream(new FileInputStream(files[fileIndex]));

		// Reset the counter for this file
		bytesRead[fileIndex] = 0;

		return is;
	}

	/** Returns true if this reader can understand the file given to it. */
	abstract boolean canRead()
		throws Exception;
}