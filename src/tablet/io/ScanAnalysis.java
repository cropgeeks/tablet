// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;

import tablet.analysis.*;
import tablet.gui.scanner.*;
import static tablet.io.AssemblyFileHandler.*;

public class ScanAnalysis extends SimpleJob
{
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
		{ return scanFile.getPath(); }

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

		if (file.isDirectory())
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
			AssemblyFile aFile = new AssemblyFile(file.getPath());

			// Determine assembly type:
			Boolean okToRun = Boolean.TRUE;
			int type = AssemblyFileHandler.getType(file.getPath(), okToRun);

			if (type == UNKNOWN)
				return;

			FileScanner scanner = new FileScanner(aFile, type);
			scanner.scan();
			assemblyCount++;

			results.addNewResult(
				scanner.file,
				scanner.getType(),
				scanner.isPaired);
		}
	}
}