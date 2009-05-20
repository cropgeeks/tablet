package tablet.io;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.cache.*;
import tablet.gui.*;

/**
 * This is a complicated class, that presents itself as a trackable job, but in
 * reality actually lets other trackable implementations do most of the work.
 */
public class ImportHandler implements ITrackableJob
{
	private File file;
	private int jobIndex = 0;

	private ITrackableJob currentJob = null;
	private AssemblyReader reader = null;

	private Assembly assembly;
	private IReadCache readCache;

	public ImportHandler(String filename)
	{
		file = new File(filename);
	}

	// Decide which part of the import operation to run; then run it
	public void runJob(int jobIndex)
		throws Exception
	{
		this.jobIndex = jobIndex;

		if (jobIndex == 0)
			readFile();

		else if (jobIndex == 1)
		{
			currentJob = new BasePositionComparator(assembly);
			currentJob.runJob(0);
		}

		else if (jobIndex == 2)
		{
			currentJob = new PackSetCreator(assembly);
			currentJob.runJob(0);
		}
	}

	// TODO: catch failures properly; check read cache is closed on fail
	public void readFile()
		throws Exception
	{
		boolean ok = false;

		// Set up the read cache
		File cacheFile = new File("tablet-cache.dat");
		File indexFile = new File("tablet-index.dat");
		readCache = FileCache.createWritableCache(cacheFile, indexFile);

		// For each file format that we understand...

		// ACE
		reader = new AceFileReader(true);
		if (ok == false)
			ok = readFile(reader);

		if (ok == false)
		{
			// Use next reader type
		}

		if (ok)
		{
			assembly = reader.getAssembly();
			assembly.setName(file.getName());

			readCache.close();
			assembly.setReadCache(
				FileCache.createReadableCache(cacheFile, indexFile));

			// Sort the reads into order
			System.out.println("Sorting...");
			for (Contig contig: assembly.getContigs())
			{
				Collections.sort(contig.getReads());
				contig.determineOffsets();
			}
		}
		else
			throw new ReadException(ReadException.UNKNOWN_FORMAT);
	}

	private boolean readFile(AssemblyReader reader)
		throws Exception
	{
		// Try various ways of opening the file...
		InputStream is = null;

		// 1) Is it a zip file?
		if (is == null)
		{
			try
			{
				ZipFile zip = new ZipFile(file);

				Enumeration<? extends ZipEntry> entries = zip.entries();
				while (entries.hasMoreElements())
				{
					ZipEntry entry = entries.nextElement();
					System.out.println("Zip: " + file + " (" + entry + ")");
					is = zip.getInputStream(entry);
					break;
				}
			}
			catch (Exception e) {}
		}

		// 2) Is it a gzip file?
		if (is == null)
		{
			try
			{
				is = new GZIPInputStream(new FileInputStream(file));
				System.out.println("GZip: " + file);
			}
			catch (Exception e) {}
		}

		// 3) Is it a normal file?
		if (is == null)
		{
			System.out.println("Normal: " + file);
			is = new FileInputStream(file);
		}


		try
		{
			long s = System.currentTimeMillis();
			reader.setParameters(readCache, is);

			currentJob = reader;
			currentJob.runJob(1);
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

	public Assembly getAssembly()
		{ return assembly; }

	// We have 3 tasks to run; the main read, and 2 post-import tasks
	public int getJobCount()
		{ return 3; }

	public void cancelJob()
	{
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