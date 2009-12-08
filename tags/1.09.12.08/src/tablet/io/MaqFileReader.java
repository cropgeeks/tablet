// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.cache.*;

import scri.commons.gui.*;

class MaqFileReader extends TrackableReader
{
	private IReadCache readCache;

	private ReferenceFileReader refReader;

	// The index of the Maq file in the files[] array
	private int maqIndex = -1;
	// The index of the reference file in the files[] array
	private int refIndex = -1;

	private HashMap<String, Contig> contigHash = new HashMap<String, Contig>();

	private int readID = 0;

	MaqFileReader()
	{
	}

	MaqFileReader(IReadCache readCache)
	{
		this.readCache = readCache;
	}

	boolean canRead()
		throws Exception
	{
		refReader = new ReferenceFileReader(assembly, contigHash);

		// We need to check each file to see if it is readable
		for (int i = 0; i < files.length; i++)
		{
			if (isMaqFile(i))
				maqIndex = i;

			else if (refReader.canRead(files[i]) != AssemblyFileHandler.UNKNOWN)
				refIndex = i;
		}

		return (maqIndex >= 0);
	}

	// Checks to see if this is a Maq file by assuming 16 columns of \t data
	private boolean isMaqFile(int fileIndex)
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(fileIndex)));
		str = readLine();

		boolean isMaqFile = (str != null && str.split("\t").length == 16);
		if (isMaqFile)
		{
			String strand = str.split("\t")[3];
			if (!strand.equals("-") && !strand.equals("+"))
				isMaqFile = false;
		}

		in.close();
		is.close();

		return isMaqFile;
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		// Read reference information (if it exists)
		if (refIndex >= 0)
			readReferenceFile();

		// Then read the main assembly/read data file
		readMaqFile();
	}

	private void readReferenceFile()
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(refIndex), "ASCII"));

		refReader.readReferenceFile(this, files[refIndex]);

		in.close();
	}

	private void readMaqFile()
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(maqIndex), "ASCII"));

		readID = 0;

		while ((str = readLine()) != null && okToRun)
		{
			String[] tokens = str.split("\t");

			String name = new String(tokens[0]);
			String data = new String(tokens[14]);
			String chr  = tokens[1];
			boolean complemented = tokens[3].equals("-");
			int pos = Integer.parseInt(tokens[2]) - 1;

			Read read = new Read(readID, pos);

			Contig contigToAddTo = contigHash.get(chr);

			// If it wasn't found (and we don't have ref data), make it
			if (contigToAddTo == null && refIndex == -1)
			{
				contigToAddTo = new Contig(chr);
				contigHash.put(chr, contigToAddTo);

				assembly.addContig(contigToAddTo);
			}

			if (contigToAddTo != null)
			{
				contigToAddTo.getReads().add(read);

				ReadMetaData rmd = new ReadMetaData(name, complemented);
				rmd.setData(data.toString());
				rmd.calculateUnpaddedLength();
				read.setLength(rmd.length());

				// Do base-position comparison...
				BasePositionComparator.compare(contigToAddTo.getConsensus(), rmd,
					read.getStartPosition());

				readCache.setReadMetaData(rmd);

				readID++;
			}
		}

		in.close();

		assembly.setName(files[maqIndex].getName());
	}

	public String getMessage()
	{
		return RB.format("io.AssemblyFileHandler.status",
			contigHash.size(), readID);
	}
}