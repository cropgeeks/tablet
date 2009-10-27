// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.cache.*;

import scri.commons.file.*;

class SoapFileReader extends TrackableReader
{
	private IReadCache readCache;

	private ReferenceFileReader refReader;

	// The index of the SOAP file in the files[] array
	private int soapIndex = -1;
	// The index of the reference file in the files[] array
	private int refIndex = -1;

	private HashMap<String, Contig> contigHash;

	SoapFileReader(IReadCache readCache)
	{
		this.readCache = readCache;
	}

	boolean canRead()
		throws Exception
	{
		refReader = new ReferenceFileReader(assembly);

		boolean foundSoap = false;
		boolean foundRef  = false;

		// We need to check each file to see if it is readable
		for (int i = 0; i < 2; i++)
		{
			if (isSoapFile(i))
			{
				foundSoap = true;
				soapIndex = i;
			}
			else if (refReader.canRead(files[i]))
			{
				foundRef = true;
				refIndex = i;
			}
		}

		return (foundSoap && foundRef);
	}

	// Checks to see if this is a SOAP file by assuming 10 columns of \t data
	private boolean isSoapFile(int fileIndex)
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(fileIndex)));
		str = readLine();

		boolean isSoapFile = (str != null && str.split("\t").length >= 9);
		in.close();
		is.close();

		return isSoapFile;
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		readReferenceFile();
		readSoapFile();
	}

	private void readReferenceFile()
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(refIndex), "ASCII"));

		refReader.readReferenceFile(this, files[refIndex]);
		contigHash = refReader.getContigHashMap();

		in.close();

		assembly.setName(files[soapIndex].getName());
	}

	private void readSoapFile()
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(soapIndex), "ASCII"));

		int readID = 0;

		while ((str = readLine()) != null && okToRun)
		{
			String[] tokens = str.split("\t");

			String name = new String(tokens[0]);
			String data = new String(tokens[1]);
			String chr  = tokens[7];
			boolean complemented = tokens[6].equals("-");
			int pos = Integer.parseInt(tokens[8]) - 1;

			Read read = new Read(readID, pos);

			Contig contigToAddTo = contigHash.get(chr);

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
	}
}