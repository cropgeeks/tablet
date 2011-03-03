// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io.samtools;

import java.io.*;
import java.util.*;
import javax.swing.*;

import tablet.data.*;
import tablet.gui.*;
import tablet.io.*;

import scri.commons.gui.*;

public class IdxStats
{
	private HashMap<String, Contig> contigHash;
	private String cacheID;

	public IdxStats(HashMap<String, Contig> contigHash, String cacheID)
	{
		this.contigHash = contigHash;
		this.cacheID = cacheID;
	}

	public boolean run(AssemblyFile bamFile, AssemblyFile baiFile)
	{
		try
		{
			File samtools = extractSamtools();

			System.out.println("Running IDXSTATS on " + baiFile.getPath());

			ProcessBuilder pb = new ProcessBuilder(samtools.getPath(),
				"idxstats", bamFile.getPath());

			pb.directory(new File(baiFile.getFile().getParent()));
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

//			System.out.println(line);
		}
	}

	private File extractSamtools()
		throws Exception
	{
		if (Prefs.ioSamtoolsPath.length() > 0)
			return new File(Prefs.ioSamtoolsPath);

		long s = System.currentTimeMillis();

		// Decide where to extract the executable to
		File cacheDir = new File(Prefs.cacheDir);
		File samtools = new File(cacheDir, "Tablet-" + cacheID + "-samtools");

		// Read it from samtools-OS.jar
		String path = null;
		if (SystemUtils.isWindows())
			path = "/windows/samtools.exe";
		else if (SystemUtils.isMacOS())
			path = "/macos/samtools";
		else if (SystemUtils.isLinux())
			path = "/linux/samtools";

		InputStream src = IdxStats.class.getResource(path).openStream();
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