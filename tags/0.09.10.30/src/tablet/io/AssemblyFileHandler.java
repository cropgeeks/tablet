// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.cache.*;

/**
 * The AssemblyFileHandler class is given file pointers and will attempt to
 * a) find out which of the assembly file formats that Tablet understands is
 * capable of reading those file types, then b) proceed with the read/import.
 */
public class AssemblyFileHandler extends SimpleJob
{
	public static final int UNKNOWN = 0;
	public static final int ACE = 1;
	public static final int AFG = 2;
	public static final int MAQ = 3;
	public static final int SOAP = 4;
	public static final int FASTA = 5;
	public static final int FASTQ = 6;

	private File[] files = null;
	private File cacheDir = null;
	private TrackableReader reader = null;

	AssemblyFileHandler(File[] files, File cacheDir)
	{
		this.files = files;
		this.cacheDir = cacheDir;
	}

	Assembly getAssembly()
		{ return reader.getAssembly(); }

	public void runJob(int jobIndex)
		throws Exception
	{
		boolean fileParsed = false;

		// Ensure the cache directory exists (and is valid)
		cacheDir.mkdirs();

		// Set up the read cache
		String time = "" + System.currentTimeMillis();
		File cache = new File(cacheDir, time + "-" + files[0].getName() + ".cache");
		File index = new File(cacheDir, time + "-" + files[0].getName() + ".index");
		IReadCache readCache = FileCache.createWritableCache(cache, index);


		// For each file format that we understand...

		// ACE
		if (okToRun && fileParsed == false)
		{
			reader = new AceFileReader(readCache);
			fileParsed = readFile();
		}
		// AFG
		if (okToRun && fileParsed == false)
		{
			reader = new AfgFileReader(readCache, cacheDir);
			fileParsed = readFile();
		}
		// Maq
		if (okToRun && fileParsed == false)
		{
			reader = new MaqFileReader(readCache);
			fileParsed = readFile();
		}
		// SOAP
		if (okToRun && fileParsed == false)
		{
			reader = new SoapFileReader(readCache);
			fileParsed = readFile();
		}

		if (okToRun && fileParsed)
		{
			Assembly assembly = reader.getAssembly();

			readCache.close();
			assembly.setReadCache(FileCache.createReadableCache(cache, index));

			// Sort the reads into order
			System.out.print("Sorting...");
			long s = System.currentTimeMillis();
			for (Contig contig: assembly)
			{
				contig.getReads().trimToSize();
				Collections.sort(contig.getReads());
				contig.calculateOffsets();
			}
			System.out.println((System.currentTimeMillis()-s) + "ms");
		}

		// If the file couldn't be understand then throw an exception
		else if (okToRun)
			throw new ReadException(ReadException.UNKNOWN_FORMAT, 0);
	}

	// Gets a reader to check if it can read a given file, and then lets it
	// do the full read if it says it can
	private boolean readFile()
		throws Exception
	{
		reader.setInputs(files, new Assembly());

		if (reader.canRead())
		{
			long s = System.currentTimeMillis();
			reader.runJob(0);
			long e = System.currentTimeMillis();
			System.out.println("\nRead time: " + ((e-s)/1000f) + "s");

			return true;
		}

		return false;
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


	// Some additional utility methods that are used by the GUI to determine
	// file type and report back to the user - this code is NOT used to load

	public static int getType(File file)
	{
		TrackableReader reader = null;

		try
		{
			if (read(new AceFileReader(), file))
				return ACE;

			if (read(new AfgFileReader(), file))
				return AFG;

			if (read(new MaqFileReader(), file))
				return MAQ;

			if (read(new SoapFileReader(), file))
				return SOAP;

			return new ReferenceFileReader(null).canRead(file);
		}
		catch (Exception e) {}

		return UNKNOWN;
	}

	private static boolean read(TrackableReader reader, File file)
		throws Exception
	{
		reader.setInputs(new File[] { file }, null);

		return reader.canRead();
	}
}