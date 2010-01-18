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
	public static final int SAM = 3;
	public static final int MAQ = 4;
	public static final int SOAP = 5;
	public static final int FASTA = 20;
	public static final int FASTQ = 21;

	private AssemblyFile[] files = null;
	private File cacheDir = null;
	private TrackableReader reader = null;

	public AssemblyFileHandler(String[] filenames, File cacheDir)
	{
		files = new AssemblyFile[filenames.length];

		for (int i=0; i < filenames.length; i++)
			files[i] = new AssemblyFile(filenames[i]);

		this.cacheDir = cacheDir;
	}

	public Assembly getAssembly()
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
		// SAM
		if (okToRun && fileParsed == false)
		{
			reader = new SamFileReader(readCache);
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
			throw new ReadException(null, 0, ReadException.UNKNOWN_FORMAT);
	}

	// Gets a reader to check if it can read a given file, and then lets it
	// do the full read if it says it can
	private boolean readFile()
		throws ReadException
	{
		try
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
		catch(ReadException e)
		{
			throw(e);
		}
		catch(Exception e)
		{
			throw new ReadException(reader.currentFile(), reader.lineCount, e);
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

	public String getMessage()
	{
		if (reader != null)
			return reader.getMessage();

		return null;
	}


	// Some additional utility methods that are used by the GUI to determine
	// file type and report back to the user - this code is NOT used to load

	public static int getType(String filename)
	{
		TrackableReader reader = null;

		try
		{
			if (read(new AceFileReader(), filename))
				return ACE;

			if (read(new AfgFileReader(), filename))
				return AFG;

			if (read(new MaqFileReader(), filename))
				return MAQ;

			if (read(new SamFileReader(), filename))
				return SAM;

			if (read(new SoapFileReader(), filename))
				return SOAP;

			return new ReferenceFileReader(null, null).canRead(
				new AssemblyFile(filename));
		}
		catch (Exception e) {}

		return UNKNOWN;
	}

	private static boolean read(TrackableReader reader, String filename)
		throws Exception
	{
		AssemblyFile[] files = { new AssemblyFile(filename) };
		reader.setInputs(files, null);

		return reader.canRead();
	}
}