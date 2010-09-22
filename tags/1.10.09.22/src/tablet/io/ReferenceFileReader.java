// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;

import tablet.data.*;
import static tablet.io.AssemblyFile.*;

/**
 * Utility class that helps the main readers when they need to read reference
 * or consensus file information that is stored in an accompanying file.
 */
class ReferenceFileReader
{
	private Assembly assembly;
	private HashMap<String, Contig> contigHash;

	ReferenceFileReader(Assembly assembly, HashMap<String, Contig> contigHash)
	{
		this.assembly = assembly;
		this.contigHash = contigHash;
	}

	boolean canRead(AssemblyFile file)
		throws Exception
	{
		return (file.getType() == FASTA || file.getType() == FASTQ);
	}

	void readReferenceFile(TrackableReader reader, AssemblyFile file)
		throws Exception
	{
		if (file.getType() == AssemblyFile.FASTA)
			readFastaFile(reader);

		else if (file.getType() == AssemblyFile.FASTQ)
			readFastqFile(reader);
	}


	private void readFastaFile(TrackableReader reader)
		throws Exception
	{
		Contig contig = null;
		StringBuilder sb = null;
		String str = null;

		while ((str = reader.readLine()) != null && reader.okToRun())
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
	}

	private void addContig(Contig contig, Consensus consensus, StringBuilder sb)
	{
		consensus.setData(sb);
		consensus.calculateUnpaddedLength();
		contig.setConsensusSequence(consensus);

		assembly.addContig(contig);
	}

	private void readFastqFile(TrackableReader reader)
		throws Exception
	{
		Contig contig = null;
		StringBuilder sb = null;
		StringBuilder qlt = null;
		String str = null;

		while ((str = reader.readLine()) != null && reader.okToRun())
		{
			if (str.startsWith("@"))
			{
				sb = new StringBuilder();

				String name;
				if (str.indexOf(" ") != -1)
					name = str.substring(1, str.indexOf(" "));
				else
					name = str.substring(1);

				contig = new Contig(name, true, 0);
				contigHash.put(name, contig);
			}

			//Process quality data to avoid incorrectly catching @ markers
			else if(str.startsWith("+"))
			{
				int length = sb.length();
				qlt = new StringBuilder();
				while(qlt.length() != length)
				{
					str = reader.readLine();
					qlt.append(str.trim());
				}
				if (sb != null && sb.length() > 0 && qlt != null && qlt.length() > 0)
					addContig(contig, new Consensus(), sb, qlt);
			}

			else
			{
				sb.append(str.trim());
			}
		}
	}

	private void addContig(Contig contig, Consensus consensus, StringBuilder sb, StringBuilder qlt)
	{
		consensus.setData(sb);
		consensus.calculateUnpaddedLength();
		contig.setConsensusSequence(consensus);

		byte[] bq = new byte[consensus.length()];
		for(int i = 0; i < bq.length; i++)
		{
			bq[i] = (byte) (qlt.charAt(i) - 33);
			if(bq[i] > 100)
				bq[i] = 100;
		}
		consensus.setBaseQualities(bq);

		assembly.addContig(contig);
	}
}