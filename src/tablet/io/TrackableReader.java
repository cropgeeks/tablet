// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.text.*;
import java.util.zip.*;

import tablet.analysis.*;
import tablet.data.*;

import scri.commons.file.*;

/**
 * Tracks reading from one or more files. Tracking is based on all the files
 * together so will go from 0 to (total size of all files).
 */
abstract class TrackableReader extends SimpleJob
{
	private static DecimalFormat df = new DecimalFormat("0.0");

	// With multiple files in the array (max of 2), which is which?
	protected static int ASBINDEX = 0;
	protected static int REFINDEX = 1;

	// Read data
	protected AssemblyFile[] files;
	protected ProgressInputStream is;
	protected BufferedReader in;
	protected String str;
	protected int lineCount;

	protected Assembly assembly;

	// Tracks how many bytes have been read from each of the files
	private long[] bytesRead;
	// Tracks the current file being read from
	private int fileIndex;

	private boolean isIndeterminate = false;

	// The total size (in bytes) of all the files
	private long totalSize;

	// Tracking variables for transfer rate (MB/s) info
	private long lastBytesRead;
	private long lastTime;

	void setInputs(AssemblyFile[] files, Assembly assembly)
	{
		this.files = files;
		this.assembly = assembly;

		bytesRead = new long[files.length];

		for (AssemblyFile file: files)
		{
			long length = file.length();

			// Special check to deal with files whose length can't be determined
			// (usually happens with web server files returning -1 for length)
			if (length <= 0)
				isIndeterminate = true;

			totalSize += length;
		}
	}

	String readLine()
		throws IOException
	{
		lineCount++;
		return in.readLine();
	}

	public Assembly getAssembly()
		{ return assembly; }

	public boolean isIndeterminate()
		{ return isIndeterminate; }

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

	InputStream getInputStream(int fileIndex)
		throws Exception
	{
		this.fileIndex = fileIndex;

		// Reset the counter for this file
		lastBytesRead = bytesRead[fileIndex] = 0;
		lastTime = System.currentTimeMillis();

		try
		{
			// We always open the file itself (as this tracks the bytes read)
			is = new ProgressInputStream(files[fileIndex].getInputStream());

			// But we might have a gzip file, and the actual stream we want to
			// read is inside of it
			GZIPInputStream zis = new GZIPInputStream(is);
			return zis;
		}
		catch (Exception e) { is.close(); }

		// If not, just return the normal stream
		is = new ProgressInputStream(files[fileIndex].getInputStream());

		return is;
	}

	AssemblyFile currentFile()
	{
		return files[fileIndex];
	}

	String getTransferRate()
	{
		// Time between reads
		long timeDiff = System.currentTimeMillis() - lastTime;
		long byteDiff = bytesRead[fileIndex] - lastBytesRead;

		float bytesPerSec = byteDiff / (float) (timeDiff / (float) 1000);
		float kbPerSec = bytesPerSec / (float) 1024;

		// TODO: Uncomment for actual per second rate, rather than just the
		// average rate since the file load/download began
//		lastBytesRead = bytesRead[fileIndex];
//		lastTime = System.currentTimeMillis();

		if (kbPerSec >= 1024)
		{
			float mbPerSec = kbPerSec / (float) 1024;
			return df.format(mbPerSec) + " MB/sec";
		}
		else
			return df.format(kbPerSec) + " KB/sec";
	}
}