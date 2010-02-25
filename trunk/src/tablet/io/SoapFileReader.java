// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.cache.*;

import scri.commons.gui.*;

class SoapFileReader extends TrackableReader
{
	private IReadCache readCache;

	private ReferenceFileReader refReader;

	// The index of the SOAP file in the files[] array
	private int soapIndex = -1;
	// The index of the reference file in the files[] array
	private int refIndex = -1;

	private HashMap<String, Contig> contigHash = new HashMap<String, Contig>();

	private int readID = 0;

	SoapFileReader()
	{
	}

	SoapFileReader(IReadCache readCache)
	{
		this.readCache = readCache;
	}

	public boolean canRead()
		throws Exception
	{
		refReader = new ReferenceFileReader(assembly, contigHash);

		// We need to check each file to see if it is readable
		for (int i = 0; i < files.length; i++)
		{
			if (isSoapFile(i))
				soapIndex = i;

			else if (refReader.canRead(files[i]) != AssemblyFileHandler.UNKNOWN)
				refIndex = i;
		}

		return (soapIndex >= 0);
	}

	// Checks to see if this is a SOAP file by assuming 10 columns of \t data
	private boolean isSoapFile(int fileIndex)
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(fileIndex, true)));
		str = readLine();

		boolean isSoapFile = (str != null && str.split("\t").length >= 9);
		in.close();
		is.close();

		return isSoapFile;
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		// Read reference information (if it exists)
		if (refIndex >= 0)
			readReferenceFile();

		// Then read the main assembly/read data file
		readSoapFile();
	}

	private void readReferenceFile()
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(refIndex, true), "ASCII"));

		refReader.readReferenceFile(this, files[refIndex]);

		in.close();
	}

	private void readSoapFile()
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(soapIndex, true), "ASCII"));

		readID = 0;

		while ((str = readLine()) != null && okToRun)
		{
			String[] tokens = str.split("\t");

			String name = new String(tokens[0]);
			StringBuilder data = new StringBuilder(tokens[1]);
			String chr  = tokens[7];
			boolean complemented = tokens[6].equals("-");
			int pos = Integer.parseInt(tokens[8]) - 1;

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
				rmd.setData(data);
				rmd.calculateUnpaddedLength();
				read.setLength(rmd.length());

				// Do base-position comparison...
				BasePositionComparator.compare(contigToAddTo, rmd,
					read.getStartPosition());

				readCache.setReadMetaData(rmd);

				readID++;
			}
		}

		in.close();

		assembly.setName(files[soapIndex].getName());
	}

	public String getMessage()
	{
		return RB.format("io.AssemblyFileHandler.status",
			getTransferRate(), contigHash.size(), readID);
	}
}