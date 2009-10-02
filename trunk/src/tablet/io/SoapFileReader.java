// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import tablet.data.*;
import tablet.data.cache.*;
import tablet.data.auxiliary.*;
import static tablet.io.ReadException.*;

import scri.commons.file.*;

class SoapFileReader extends TrackableReader
{
	private boolean useAscii;
	private IReadCache readCache;

	// The index of the SOAP file in the files[] array
	private int soapIndex = -1;
	// The index of the FASTA file in the files[] array
	private int fastaIndex = -1;

	private HashMap<String, Contig> contigHash = new HashMap<String, Contig>();

	SoapFileReader(IReadCache readCache, boolean useAscii)
	{
		this.readCache = readCache;
		this.useAscii = useAscii;
	}

	boolean canRead()
		throws Exception
	{
		boolean foundSoap = false;
		boolean foundFasta = false;

		// We need to check each file to see if it is readable
		for (int i = 0; i < 2; i++)
		{
			if (isSoapFile(i))
			{
				foundSoap = true;
				soapIndex = i;
			}
			else if (isFastaFile(i))
			{
				foundFasta = true;
				fastaIndex = i;
			}
		}

		return (foundSoap && foundFasta);
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

	// Checks to see if this is a FASTA file by looking for a leading >
	private boolean isFastaFile(int fileIndex)
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(fileIndex)));
		str = readLine();

		boolean isFastaFile = (str != null && str.startsWith(">"));
		in.close();
		is.close();

		return isFastaFile;
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		readFastaFile();
		readSoapFile();
	}

	private void readFastaFile()
		throws Exception
	{
		if (useAscii)
			in = new BufferedReader(new InputStreamReader(getInputStream(fastaIndex), "ASCII")); // ISO8859_1
		else
			in = new BufferedReader(new InputStreamReader(getInputStream(fastaIndex)));

		System.out.println("FASTA: " + files[fastaIndex]);

		Contig contig = null;
		StringBuilder sb = null;

		while ((str = readLine()) != null && okToRun)
		{
			if (str.startsWith(">"))
			{
				if (sb != null && sb.length() > 0)
					addContig(contig, new Consensus(), sb);

				sb = new StringBuilder();

				String name;
				if (str.indexOf(" ") != -1)
					name = str.substring(1, str.indexOf(" "));
				else
					name = str.substring(1);

				contig = new Contig(name, true, 0);
				contigHash.put(name, contig);
			}

			else
				sb.append(str.trim());
		}

		if (sb != null && sb.length() > 0)
			addContig(contig, new Consensus(), sb);

		in.close();

		assembly.setName(files[soapIndex].getName());
	}

	private void readSoapFile()
		throws Exception
	{
		if (useAscii)
			in = new BufferedReader(new InputStreamReader(getInputStream(soapIndex), "ASCII")); // ISO8859_1
		else
			in = new BufferedReader(new InputStreamReader(getInputStream(soapIndex)));

		System.out.println("SOAP:  " + files[soapIndex]);

		int readID = 0;

		while ((str = readLine()) != null && okToRun)
		{
			String[] tokens = str.split("\t");

			String name = new String(tokens[0]);
			String data = tokens[1];
			String chr  = tokens[7];
			boolean complemented = tokens[6].equals("-");
			int pos = Integer.parseInt(tokens[8]) - 1;

			Read read = new Read(readID, pos);
			read.setData(data.toString());


			Contig contigToAddTo = contigHash.get(chr);

			if (contigToAddTo != null)
			{
				contigToAddTo.getReads().add(read);

				ReadMetaData rmd = new ReadMetaData(
					name, complemented, read.calculateUnpaddedLength());
				readCache.setReadMetaData(rmd);
				readID++;
			}
		}

		in.close();
	}

	private void addContig(Contig contig, Consensus consensus, StringBuilder sb)
	{
		consensus.setData(sb.toString());
		consensus.calculateUnpaddedLength();
		contig.setConsensusSequence(consensus);

		byte[] bq = new byte[consensus.length()];
		consensus.setBaseQualities(bq);

		assembly.addContig(contig);
	}
}