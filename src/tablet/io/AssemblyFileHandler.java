// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.cache.*;
import tablet.gui.*;
import static tablet.io.AssemblyFile.*;

import scri.commons.gui.*;

/**
 * The AssemblyFileHandler class is given file pointers and will attempt to
 * a) find out which of the assembly file formats that Tablet understands is
 * capable of reading those file types, then b) proceed with the read/import.
 */
public class AssemblyFileHandler extends SimpleJob
{
	private AssemblyFile[] files = null;
	private File cacheDir = null;
	private TrackableReader reader = null;

	private String cacheid = SystemUtils.createGUID(24);

	private Assembly assembly;

	public AssemblyFileHandler(String[] filenames, File cacheDir)
	{
		files = new AssemblyFile[filenames.length];

		for (int i=0; i < filenames.length; i++)
			files[i] = new AssemblyFile(filenames[i]);

		this.cacheDir = cacheDir;
	}

	public Assembly getAssembly()
		{ return assembly; }

	public void runJob(int jobIndex)
		throws Exception
	{
		// Ensure the cache directory exists (and is valid)
		cacheDir.mkdirs();

		// Set up the read cache
		IReadCache readCache = null;

		if (Prefs.cacheReads)
		{
			File cache = new File(cacheDir, "Tablet-" + cacheid + ".reads");
			File index = new File(cacheDir, "Tablet-" + cacheid + ".readsndx");
			readCache = new ReadFileCache(cache, index);
		}
		else
			readCache = new ReadMemCache();

		readCache.openForWriting();

		// Determine assembly type and sort into order (assembly before ref)
		for (AssemblyFile aFile: files)
			aFile.canDetermineType();

		Arrays.sort(files);


		// For each file format that we understand...
		switch (files[0].getType())
		{
			case ACE:
				reader = new AceFileReader(readCache);
				break;

			case AFG:
				reader = new AfgFileReader(readCache, cacheDir);
				break;

			case SAM:
				reader = new SamFileReader(readCache);
				break;

			case BAM:
				reader = new BamFileReader(readCache, cacheDir, cacheid);
				break;

			case MAQ:
				reader = new MaqFileReader(readCache);
				break;

			case SOAP:
				reader = new SoapFileReader(readCache);
				break;
		}

		if (reader != null)
		{
			readFile();

			if (okToRun == false)
				return;

			assembly = reader.getAssembly();

			readCache.openForReading();
			assembly.setReadCache(readCache);

			// Sort the reads into order
			System.out.print("Sorting...");
			long s = System.currentTimeMillis();
			for (Contig contig: assembly)
			{
				contig.getReads().trimToSize();
				Collections.sort(contig.getReads());
				contig.calculateOffsets(assembly);
			}
			System.out.println((System.currentTimeMillis()-s) + "ms");
		}

		// If the file couldn't be understood then throw an exception
		else if (okToRun)
			throw new ReadException(null, 0, ReadException.UNKNOWN_FORMAT);
	}

	private void readFile()
		throws Exception
	{
		try
		{
			reader.setInputs(files, new Assembly(cacheid));

			long s = System.currentTimeMillis();
			reader.runJob(0);
			long e = System.currentTimeMillis();
			System.out.println("\nRead time: " + ((e-s)/1000f) + "s");
		}
		catch(ReadException e)
		{
			e.printStackTrace();
			throw(e);
		}
		catch(Exception e)
		{
			e.printStackTrace();

			if (reader instanceof BamFileReader)
				throw e;

			else
			{
				TrackableReader r = (TrackableReader) reader;
				throw new ReadException(r.currentFile(), r.lineCount, e);
			}
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

	public static int getType(String file, Boolean okToRun)
	{
		AssemblyFile aFile = new AssemblyFile(file);

		aFile.canDetermineType();

		return aFile.getType();
	}
}