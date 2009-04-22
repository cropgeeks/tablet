package av.io;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import av.data.*;

public class TestReader
{
	private Assembly assembly;

	public static void main(String[] args)
		throws Exception
	{
		new TestReader(args[0]);
	}

	public TestReader(String filename)
		throws Exception
	{
		File file = new File(filename);

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


		long s = System.currentTimeMillis();
		AceFileReader reader = new AceFileReader(is, true);
		assembly = reader.readAssembly();
		long e = System.currentTimeMillis();

		System.out.println("\nRead time: " + ((e-s)/1000f) + "s");

		s = System.currentTimeMillis();
		PostImportOperations pio = new PostImportOperations(assembly);
		pio.sortReads();
		pio.compareBases();
		pio.createPackSet();
		e = System.currentTimeMillis();

		System.out.println("Post time: " + ((e-s)/1000f) + "s");

		System.out.println();
//		assembly.print();
	}

	public Assembly getAssembly()
	{
		return assembly;
	}
}