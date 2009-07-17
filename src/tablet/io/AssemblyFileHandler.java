package tablet.io;

import java.io.*;
import java.util.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.cache.*;
import tablet.gui.*;

import scri.commons.file.*;
import scri.commons.gui.*;

/**
 * The AssemblyFileHandler class is given a file pointer and will attempt to
 * a) find out which of the assembly file formats that Tablet understands is
 * capable of reading that file type, then b) proceed with the read/import.
 */
class AssemblyFileHandler extends SimpleJob
{
	private File file = null;
	private TrackableReader reader = null;

	AssemblyFileHandler(File file)
	{
		this.file = file;
	}

	Assembly getAssembly()
		{ return reader.getAssembly(); }

	public void runJob(int jobIndex)
		throws Exception
	{
		boolean fileParsed = false;

		File cacheDir = SystemUtils.getTempUserDirectory("scri-tablet");

		// Set up the read cache
		String time = "" + System.currentTimeMillis();
		File cache = new File(cacheDir, time + "-" + file.getName() + ".cache");
		File index = new File(cacheDir, time + "-" + file.getName() + ".index");
		IReadCache readCache = FileCache.createWritableCache(cache, index);


		// For each file format that we understand...

		// ACE
		if (okToRun && fileParsed == false)
		{
			reader = new AceFileReader(readCache, true);
			fileParsed = readFile();
		}

		if (okToRun && fileParsed == false)
		{
			// Use next reader type
		}

		if (okToRun && fileParsed)
		{
			Assembly assembly = reader.getAssembly();
			assembly.setName(file.getName());

			readCache.close();
			assembly.setReadCache(FileCache.createReadableCache(cache, index));

			// Sort the reads into order
			System.out.print("Sorting...");
			long s = System.currentTimeMillis();
			for (Contig contig: assembly)
			{
				Collections.sort(contig.getReads());
				contig.calculateOffsets();
			}
			System.out.println((System.currentTimeMillis()-s) + "ms");
		}

		// If the file couldn't be understand then throw an exception
		else if (okToRun)
			throw new ReadException(ReadException.UNKNOWN_FORMAT, 0);
	}

	private boolean readFile()
		throws Exception
	{
		ProgressInputStream is = new ProgressInputStream(
			new FileInputStream(file));
		is.setSize(file.length());

		reader.setInputs(is, new Assembly());

		try
		{
			long s = System.currentTimeMillis();

			reader.runJob(1);

			long e = System.currentTimeMillis();
			System.out.println("\nRead time: " + ((e-s)/1000f) + "s");

			return true;
		}
		catch (ReadException e)
		{
			// If the failure was due to not understanding the input stream,
			// then return gracefully and let another file reader try
			if (e.getError() == ReadException.UNKNOWN_FORMAT)
				return false;
			// Otherwise, it must be a genuine failure
			else
				throw e;
		}
		finally
		{
			is.close();
		}
	}

	public boolean isIndeterminate()
	{
		if (reader != null)
			return reader.isIndeterminate();
		else
			return true;
	}

	public int getMaximum()
	{
		if (reader != null)
			return reader.getMaximum();
		else
			return 0;
	}

	public int getValue()
	{
		if (reader != null)
			return reader.getValue();
		else
			return 0;
	}

	public void cancelJob()
	{
		okToRun = false;

		if (reader != null)
			reader.cancelJob();
	}
}