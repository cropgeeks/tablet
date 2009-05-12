package tablet.io;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import tablet.data.*;
import tablet.data.cache.*;
import tablet.gui.*;

public class ImportHandler
{
	private File file;

	private Assembly assembly;
	private IReadCache readCache;

	// TODO: catch failures properly; check read cache is closed on fail

	public void readFile(String filename)
		throws Exception
	{
		file = new File(filename);
		boolean ok = false;

		AssemblyReader reader = null;

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
			readCache.close();

			assembly.setReadCache(
				FileCache.createReadableCache(cacheFile, indexFile));
		}
		else
			return;


		long s = System.currentTimeMillis();
		PostImportOperations pio = new PostImportOperations(assembly);
		pio.sortReads();
		pio.compareBases();
		pio.createPackSet();
		long e = System.currentTimeMillis();

		System.out.println("Post time: " + ((e-s)/1000f) + "s");
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
			reader.runJob();
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
	{
		return assembly;
	}
}