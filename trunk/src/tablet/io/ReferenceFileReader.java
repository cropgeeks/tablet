// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;

import tablet.data.*;
import tablet.data.cache.*;
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

	void readReferenceFile(TrackableReader reader, AssemblyFile file)
		throws Exception
	{
		File index = ConsensusFileCache.getExistingIndex(file);

		if (index != null)
			readIndex(index);

		else
		{
			if (file.getType() == AssemblyFile.FASTA)
				readFastaFile(reader);

			else if (file.getType() == AssemblyFile.FASTQ)
				readFastqFile(reader);

			if (reader.okToRun())
				saveIndex(file);
		}
	}

	private void readIndex(File file)
		throws Exception
	{
		BufferedReader in = new BufferedReader(new FileReader(file));

		// The first line gives the path to the actual cache file
		String str = in.readLine();
		File cacheFile = new File(str);
		Consensus.prepareCache(cacheFile);
		Consensus.getCache().openForReading();

		// Now read in the index data and initialise each contig+consensus
		str = in.readLine();
		while (str != null)
		{
			String name = str;
			long offset = Long.parseLong(in.readLine());
			int length = Integer.parseInt(in.readLine());

			Contig contig = new Contig(name, true, 0);
			contigHash.put(name, contig);

			Consensus consensus = new Consensus();
			consensus.setCacheOffset(assembly.size(), offset, length);
			contig.setConsensus(consensus);
			assembly.addContig(contig);

			str = in.readLine();
		}

		in.close();
	}

	// Saves a *.refs.index for this the current cached copy of the reference
	// sequence. The file writes 3 lines at a time: the contig's name, its
	// offset within the cache, and its length (in bytes). The first line is a
	// special case and contains the full path to the actual cache
	private void saveIndex(AssemblyFile rFile)
		throws Exception
	{
		File cacheFile = Consensus.getCache().getCacheFile();
		File indexFile = new File(cacheFile.getParent(), cacheFile.getName() + "ndx");

		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(indexFile)));
		out.println(cacheFile.getPath());

		for (Contig contig: assembly)
		{
			out.println(contig.getName());
			out.println(contig.getConsensus().getCacheOffset());
			out.println(contig.getConsensus().length());
		}
		out.close();

		Consensus.getCache().updateIndexList(rFile, indexFile);
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