// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

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
	private ReadSQLCache nameCache;

	private ReferenceFileReader refReader;

	private HashMap<String, Contig> contigHash = new HashMap<>();

	private int readID = 0;

	MaqFileReader()
	{
	}

	MaqFileReader(IReadCache readCache, ReadSQLCache nameCache)
	{
		this.readCache = readCache;
		this.nameCache = nameCache;
	}

	private void readReferenceFile()
		throws Exception
	{
		if (files.length > 1 && files[REFINDEX].isReferenceFile())
		{
			refReader = new ReferenceFileReader(assembly, contigHash);

			in = new BufferedReader(new InputStreamReader(getInputStream(REFINDEX), "ASCII"));

			refReader.readReferenceFile(this, files[1]);

			in.close();
		}
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		// Try and read the reference file (if there is one)
		readReferenceFile();


		in = new BufferedReader(new InputStreamReader(getInputStream(ASBINDEX), "ASCII"));

		readID = 0;

		while ((str = readLine()) != null && okToRun)
		{
			String[] tokens = str.split("\t");

			String name = new String(tokens[0]);
			StringBuilder data = new StringBuilder(tokens[14]);
			String chr  = tokens[1];
			boolean complemented = tokens[3].equals("-");
			int pos = Integer.parseInt(tokens[2]) - 1;

			Read read = new Read(readID, pos);

			Contig contigToAddTo = contigHash.get(chr);

			// If it wasn't found (and we don't have ref data), make it
			if (contigToAddTo == null && refReader == null)
			{
				contigToAddTo = new Contig(chr);
				contigHash.put(chr, contigToAddTo);

				assembly.addContig(contigToAddTo);
			}

			if (contigToAddTo != null)
			{
				contigToAddTo.getReads().add(read);

				ReadNameData rnd = new ReadNameData(name);


				ReadMetaData rmd = new ReadMetaData(complemented);
				rmd.setData(data);

				int uLength = rmd.calculateUnpaddedLength();
				rnd.setUnpaddedLength(uLength);
				nameCache.setReadNameData(rnd, contigToAddTo);

				read.setLength(rmd.length());

				// Do base-position comparison...
				BasePositionComparator.compare(contigToAddTo, rmd,
					read.s());

				readCache.setReadMetaData(rmd);

				readID++;
			}
		}

		in.close();

		Assembly.setIsPaired(false);
		assembly.setName(files[0].getName());
		assembly.getReadGroups().clear();
	}

	public String getMessage()
	{
		return RB.format("io.AssemblyFileHandler.status",
			getTransferRate(), contigHash.size(), readID);
	}
}