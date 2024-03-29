// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;

import scri.commons.gui.*;

import tablet.data.*;
import tablet.data.cache.*;
import tablet.gui.*;
import static tablet.io.AssemblyFile.*;

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
	private boolean refLengthsOK = true;

	public AssemblyFileHandler(AssemblyFile[] files, File cacheDir)
	{
		this.files = files;
		this.cacheDir = cacheDir;
	}

	public AssemblyFile[] getFiles()
		{ return files; }

	public Assembly getAssembly()
		{ return assembly; }

	private void setupCaches()
	{
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		// Ensure the cache directory exists (and is valid)
		cacheDir.mkdirs();

		// Set up the read cache
		IReadCache readCache = null;
		ReadSQLCache sqlCache = null;

		// If it's a BAM file and we don't want read caching for BAM, or we don't
		// want reach caching full stop, then just use in-memory caching
		if ((files[0].getType() == BAM && Prefs.ioNeverCacheBAM) || Prefs.ioCacheReads == false)
			readCache = new ReadMemCache();

		// Otherwise, use a disk cache
		else
		{
			File cache = new File(cacheDir, "Tablet-" + cacheid + ".reads");
			File index = new File(cacheDir, "Tablet-" + cacheid + ".readsndx");
			readCache = new ReadFileCache(cache, index);
		}

		sqlCache = new ReadSQLCache(new File(cacheDir, "Tablet-" + cacheid + ".sqlite"));
		Consensus.prepareCache(new File(cacheDir, "Tablet-" + cacheid + ".refs"));

		readCache.openForWriting();
		sqlCache.openForWriting();


		// For each file format that we understand...
		switch (files[0].getType())
		{
			case ACE:
				reader = new AceFileReader(readCache, sqlCache);
				break;

			case AFG:
				reader = new AfgFileReader(readCache, sqlCache, cacheDir);
				break;

			case SAM:
				reader = new SamFileReader(readCache, sqlCache);
				break;

			case BAM:
				reader = new BamFileReader(readCache, sqlCache, cacheDir, cacheid);
				break;

			case MAQ:
				reader = new MaqFileReader(readCache, sqlCache);
				break;

			case SOAP:
				reader = new SoapFileReader(readCache, sqlCache);
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

			sqlCache.openForReading();
			assembly.setNameCache(sqlCache);

			// Sort the reads into order
//			System.out.print("Sorting...");
//			long s = System.currentTimeMillis();
			for (Contig contig: assembly)
			{
//				contig.getReads().trimToSize();
//				//Collections.sort(contig.getReads());
				contig.calculateOffsets(assembly);
				contig.setCacheOffset();
			}
//			System.out.println((System.currentTimeMillis()-s) + "ms");
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

	public boolean refLengthsOK()
		{ return reader.refLengthsOK(); }
}