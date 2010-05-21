// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data.cache;

import java.io.*;
import java.util.concurrent.*;

import scri.commons.file.*;

public abstract class TabletCache
{
	// The file where the cache is stored
	protected File cacheFile;

	// Used while writing to the cache
	protected DataOutputStream out;

	// Used while reading from the cache
	protected BufferedRandomAccessFile rnd;

	protected Semaphore fileLock = new Semaphore(1, true);

	public void openForWriting()
		throws IOException
	{
		out = new DataOutputStream(new BufferedOutputStream(
			new FileOutputStream(cacheFile)));
	}

	public void openForReading()
		throws IOException
	{
		out.close();
		rnd = new BufferedRandomAccessFile(cacheFile, "r", 1024);
	}

	/* Closes this disk cache object. */
	public void close()
		throws IOException
	{
		if (out != null)
			out.close();

		if (rnd != null)
			rnd.close();
	}
}