package tablet.io;

import java.io.*;
import java.util.*;

import tablet.data.*;

class FaiFileReader
{
	private Assembly assembly;
	private HashMap<String, Contig> contigHash;

	FaiFileReader(Assembly assembly, HashMap<String, Contig> contigHash)
	{
		this.assembly = assembly;
		this.contigHash = contigHash;
	}

	boolean canRead(AssemblyFile file)
		throws Exception
	{
		BufferedReader in = new BufferedReader(
			new InputStreamReader(file.getInputStream()));

		String str = in.readLine();

		boolean isFastaFile = (str != null && str.startsWith(">"));
		in.close();

		return isFastaFile;
	}

	void readReferenceFile(AssemblyFile file)
		throws Exception
	{
		BufferedReader in = new BufferedReader(
			new InputStreamReader(file.getInputStream()));

		Contig contig = null;
		StringBuilder sb = null;
		String str = null;

		while ((str = in.readLine()) != null)
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