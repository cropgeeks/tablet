// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

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
		Consensus consensus = null;
		StringBuilder sb = null;
		String str = null;

		while ((str = reader.readLine()) != null && reader.okToRun())
		{
			if (str.startsWith(">"))
			{
				if (consensus != null)
					addContig(contig, consensus);

				consensus = new Consensus();

				String name;
				if (str.indexOf(" ") != -1)
					name = str.substring(1, str.indexOf(" "));
				else
					name = str.substring(1);

				contig = new Contig(name, true, 0);
				contigHash.put(name, contig);
			}

			else
				consensus.appendSequence(str.trim());
		}

		if (consensus != null)
			addContig(contig, consensus);
	}

	private void addContig(Contig contig, Consensus consensus)
		throws Exception
	{
		consensus.closeSequence();
		contig.setConsensus(consensus);

		assembly.addContig(contig);
	}

	private void readFastqFile(TrackableReader reader)
		throws Exception
	{
		Contig contig = null;
		Consensus consensus = null;
//		StringBuilder sb = null;
//		StringBuilder qlt = null;
		String str = null;

		while ((str = reader.readLine()) != null && reader.okToRun())
		{
			if (str.startsWith("@"))
			{
				consensus = new Consensus();

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
				addContig(contig, consensus);

				int length = consensus.length();
				int qltLength = 0;
//				qlt = new StringBuilder();
//				while(qlt.length() != length)
				while (qltLength != length)
				{
					str = reader.readLine();
					qltLength += str.length();
//					qlt.append(str.trim());
				}
//				if (consensus != null && qltLength > 0)
//				if (sb != null && sb.length() > 0 && qlt != null && qlt.length() > 0)
//					addContig(contig, consensus);

			}

			else
				consensus.appendSequence(str.trim());
		}
	}

	// 2011-04-06 - Unused (see commented-out addContig() call above)
	// This is to remove calls to Consensus.setBaseQualities()
/*	private void addContig(Contig contig, Consensus consensus, StringBuilder sb, StringBuilder qlt)
		throws Exception
	{
		consensus.setData(sb);
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
*/
}