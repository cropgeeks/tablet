// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io.samtools;

import java.io.*;
import java.util.*;
import javax.swing.*;

import tablet.data.*;
import tablet.gui.*;
import tablet.io.*;

import scri.commons.gui.*;

public class SamtoolsHelper
{
	// Holds the version of samtools once read from it
	private static String version;

	private HashMap<String, Contig> contigHash;
	private String cacheID;

	public SamtoolsHelper(HashMap<String, Contig> contigHash, String cacheID)
	{
		this.contigHash = contigHash;
		this.cacheID = cacheID;
	}

	public boolean run(AssemblyFile bamFile, AssemblyFile baiFile)
	{
		try
		{
			File samtools = extractSamtools(cacheID);

			System.out.println("Running IDXSTATS on " + baiFile.getPath());

			ProcessBuilder pb = new ProcessBuilder(samtools.getPath(),
				"idxstats", bamFile.getPath());

			// 21/07/2014 - BUG: If a relative path is being used, then setting
			// the working directory to where the file is breaks, as samtools
			// then goes looking for the file using a relative path from where
			// it actually is!
//			pb.directory(new File(baiFile.getFile().getAbsoluteFile().getParent()));
			pb.redirectErrorStream(true);

			Process proc = pb.start();
			IdxStatsCatcher iostream = new IdxStatsCatcher(proc.getInputStream());

			while (iostream.isRunning())
				try { Thread.sleep(10); }
				catch (InterruptedException e) {}

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public static String getVersion()
	{
		if (version != null)
			return version;

		try
		{
			File samtools = extractSamtools("" + System.currentTimeMillis());

			ProcessBuilder pb = new ProcessBuilder(samtools.getPath());
			pb.redirectErrorStream(true);

			Process proc = pb.start();

			VersionCatcher iostream = new VersionCatcher(proc.getInputStream());

			while (iostream.isRunning())
				try { Thread.sleep(10); }
				catch (InterruptedException e) {}
		}
		catch (Exception e) { System.out.println(e);}

		return (version != null) ? version : "";
	}

	private class IdxStatsCatcher extends StreamCatcher
	{
		IdxStatsCatcher(InputStream in) { super(in); }

		protected void processLine(String line)
		{
			try
			{
				String[] tokens = line.split("\t");

				if (tokens.length == 4)
				{
					String name = tokens[0];
					int readCount = Integer.parseInt(tokens[2]);

					Contig contig = contigHash.get(name);
					if (contig != null)
						contig.getTableData().readCount = readCount;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private static class VersionCatcher extends StreamCatcher
	{
		VersionCatcher(InputStream in) { super(in); }

		protected void processLine(String line)
		{
			try
			{
				if (line.startsWith("Version:"))
					version = line.substring(9);
			}
			catch (Exception e) {}
		}
	}

	private static File extractSamtools(String cacheID)
		throws Exception
	{
		if (Prefs.ioSamtoolsPath.length() > 0)
			return new File(Prefs.ioSamtoolsPath);

		long s = System.currentTimeMillis();

		// Decide where to extract the executable to
		File cacheDir = new File(Prefs.cacheFolder);
		File samtools = new File(cacheDir, "Tablet-" + cacheID + "-samtools");

		// Read it from samtools-OS.jar
		String path = null;
		if (SystemUtils.isWindows())
			path = "/windows/samtools.exe";
		else if (SystemUtils.isMacOS())
			path = "/macos/samtools";

		else if (SystemUtils.isLinux())
		{
			if (SystemUtils.isCurrentLinuxJava64Bit())
				path = "/linux64/samtools";
			else
				path = "/linux32/samtools";
		}

		InputStream src = SamtoolsHelper.class.getResource(path).openStream();
		FileOutputStream out = new FileOutputStream(samtools);

		byte[] temp = new byte[32768];
		int count = 0;
		while((count = src.read(temp)) > 0)
			out.write(temp, 0, count);
		src.close();
		out.close();

		// And finally, make it executable
		samtools.setExecutable(true);
		samtools.deleteOnExit();

		long e = System.currentTimeMillis();
		System.out.println("Samtools extracted in " + (e-s) + "ms");

		return samtools;
	}
}