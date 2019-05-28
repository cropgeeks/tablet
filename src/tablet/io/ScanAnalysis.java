// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;

import tablet.gui.scanner.*;
import static tablet.io.AssemblyFile.*;

import scri.commons.gui.*;

public class ScanAnalysis extends SimpleJob
{
	private FileScanner scanner = null;

	// File (or folder) that is to be scanned
	private File target;
	// File that is currently *being* scanned
	private File scanFile;

	// JTable object model that stores the results
	private ResultsTableModel results;

	// Number of files scanned
	private int scanCount, assemblyCount;

	public ScanAnalysis(File target, ResultsTableModel results)
	{
		this.target = target;
		this.results = results;
	}

	public int getScanCount()
		{ return scanCount; }

	public int getAssemblyCount()
		{ return assemblyCount; }

	public String getMessage()
	{
		if (scanFile != null)
			return scanFile.getPath();

		return "";
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		scan(target);
	}

	private void scan(File file)
		throws Exception
	{
		if (okToRun == false)
			return;

		if (file.isDirectory() && file.listFiles() != null)
		{
			for (File f: file.listFiles())
			{
				// Should we skip scanning sub-folders?
//				if (f.isDirectory() && true)
//					continue;

				scan(f);
			}
		}

		else
		{
			scanCount++;

			scanFile = file;

			// Determine assembly type:
			AssemblyFile aFile = new AssemblyFile(file.getPath());
			aFile.canDetermineType();
			int type = aFile.getType();

			if (type == UNKNOWN)
				return;

			scanner = new FileScanner(aFile, type);
			scanner.runJob(0);
			assemblyCount++;

			results.addNewResult(
				scanner.file,
				scanner.getType(),
				scanner.contigCount,
				scanner.readCount,
				scanner.isPaired,
				scanner.isCompressed);
		}
	}

	public void cancelJob()
	{
		super.cancelJob();

		if (scanner != null)
			scanner.cancelJob();
	}
}