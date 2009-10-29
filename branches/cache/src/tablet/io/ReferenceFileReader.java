package tablet.io;

import java.io.*;
import java.util.*;

import tablet.data.*;

/**
 * Utility class that helps the main readers when they need to read reference
 * or consensus file information that is stored in an accompanying file.
 */
class ReferenceFileReader
{
	private Assembly assembly;
	private HashMap<String, Contig> contigHash = new HashMap<String, Contig>();

	ReferenceFileReader(Assembly assembly)
	{
		this.assembly = assembly;
	}

	HashMap<String, Contig> getContigHashMap()
		{ return contigHash; }

	int canRead(File file)
		throws Exception
	{
		if (isFastaFile(file))
			return AssemblyFileHandler.FASTA;

		if (isFastqFile(file))
			return AssemblyFileHandler.FASTQ;

		System.out.println("REF UNKNOWN");
		return AssemblyFileHandler.UNKNOWN;
	}

	void readReferenceFile(TrackableReader reader, File file)
		throws Exception
	{
		if (isFastaFile(file))
			readFastaFile(reader);

		else if (isFastqFile(file))
			readFastqFile(reader);
	}

	// Checks to see if this is a FASTA file by looking for a leading >
	private boolean isFastaFile(File file)
		throws Exception
	{
		BufferedReader in = new BufferedReader(new FileReader(file));
		String str = in.readLine();

		boolean isFastaFile = (str != null && str.startsWith(">"));
		in.close();

		return isFastaFile;
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
		consensus.setData(sb.toString());
		consensus.calculateUnpaddedLength();
		contig.setConsensusSequence(consensus);

		byte[] bq = new byte[consensus.length()];
		consensus.setBaseQualities(bq);

		assembly.addContig(contig);
	}

	// Checks to see if this is a FASTQ file by looking for a leading @
	private boolean isFastqFile(File file)
		throws Exception
	{
		BufferedReader in = new BufferedReader(new FileReader(file));
		String str = in.readLine();

		boolean isFastqFile = (str != null && str.startsWith("@"));
		in.close();

		return isFastqFile;
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
		consensus.setData(sb.toString());
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