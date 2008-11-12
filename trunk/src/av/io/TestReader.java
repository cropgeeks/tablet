package av.io;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import av.data.*;

public class TestReader
{
	public static void main(String[] args)
		throws Exception
	{
		new TestReader(new File(args[0]));
	}

	TestReader(File file)
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


		long s = System.currentTimeMillis();
		AceFileReader reader = new AceFileReader(is, true);
		Assembly assembly = reader.readAssembly();
		long e = System.currentTimeMillis();

		System.out.println("\nRead time: " + ((e-s)/1000f) + "s");

		System.out.println();
//		assembly.print();
	}
}