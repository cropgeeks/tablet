// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.cache.*;
import tablet.gui.*;

import scri.commons.gui.*;

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
	public static final int BAM = 4;
	public static final int MAQ = 5;
	public static final int SOAP = 6;
	public static final int FASTA = 20;
	public static final int FASTQ = 21;

	private AssemblyFile[] files = null;
	private File cacheDir = null;
	private IAssemblyReader reader = null;

	private String cacheid = SystemUtils.createGUID(24);
	private boolean bai = false;

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
		boolean fileParsed = false;
		bai = false;

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


		// For each file format that we understand...

		if(okToRun && fileParsed == false)
		{
			reader = new BAIFileReader(readCache, cacheDir, cacheid);
			fileParsed = readFile();
			bai = true;
		}

		if (fileParsed == false)
			System.exit(0);

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
		//BAM
//		if(okToRun && fileParsed == false)
//		{
//			reader = new BamFileReader(readCache);
//			fileParsed = readFile();
//		}
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
			reader.setInputs(files, new Assembly(cacheid));

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
			e.printStackTrace();
			throw(e);
		}
		catch(Exception e)
		{
			e.printStackTrace();

			if (reader instanceof TrackableReader)
			{
				TrackableReader r = (TrackableReader) reader;
				throw new ReadException(r.currentFile(), r.lineCount, e);
			}
			else
				throw new ReadException(null, 0, 0);
		}
	}

//	private boolean readBAIFile()
//			throws ReadException
//	{
//		try
//		{
//			baiReader.setInputs(files, new Assembly(cacheid));
//
//			if(baiReader.canRead())
//			{
//				long s = System.currentTimeMillis();
//				baiReader.runJob();
//				long e = System.currentTimeMillis();
//				System.out.println("\nRead time: " + ((e-s)/1000f) + "s");
//
//				return true;
//			}
//			else
//			{
//				System.exit(1);
//			}
//			return false;
//		}
//		catch(ReadException e)
//		{
//			e.printStackTrace();
//			throw(e);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			//TODO update this to read off BAIFileReader
//			throw new ReadException(files[0], 0, e);
//		}
//	}

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

			if (read(new BamFileReader(), filename))
				return BAM;

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

interface IAssemblyReader
{
	public boolean canRead() throws Exception;

	public void runJob(int index) throws Exception;

	public void setInputs(AssemblyFile[] files, Assembly assembly);

	public Assembly getAssembly();

	public boolean isIndeterminate();

	public int getMaximum();

	public int getValue();

	public void cancelJob();

	public String getMessage();
}