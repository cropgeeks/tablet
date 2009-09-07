package tablet.io;

import java.io.*;
import java.util.*;

import tablet.data.*;
import tablet.data.cache.IReadCache;

public class MaqFileReader extends TrackableReader
{
	private boolean useAscii;
	private IReadCache readCache;

	// The index of the Maq file in the files[] array
	private int maqIndex = -1;
	// The index of the FASTQ file in the files[] array
	private int fastqIndex = -1;

	private HashMap<String, Contig> contigHash = new HashMap<String, Contig>();

	MaqFileReader(IReadCache readCache, boolean useAscii)
	{
		this.readCache = readCache;
		this.useAscii = useAscii;
	}

	@Override
	boolean canRead() throws Exception
	{
		boolean foundMaq = false;
		boolean foundFastq = false;

		// We need to check each file to see if it is readable
		for (int i = 0; i < 2; i++)
		{
			if (isMaqFile(i))
			{
				foundMaq = true;
				maqIndex = i;
			}
			else if (isFastqFile(i))
			{
				foundFastq = true;
				fastqIndex = i;
			}
		}

		return (foundMaq && foundFastq);
	}

	public void runJob(int jobIndex) throws Exception
	{
		readFastqFile();
		readMaqFile();
	}

		// Checks to see if this is a Maq file by assuming 16 columns of \t data
	private boolean isMaqFile(int fileIndex)
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(fileIndex)));
		str = readLine();

		boolean isMaqFile = (str != null && str.split("\t").length == 16);
		in.close();
		is.close();

		return isMaqFile;
	}

	// Checks to see if this is a FASTQ file by looking for a leading @
	private boolean isFastqFile(int fileIndex)
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(fileIndex)));
		str = readLine();

		boolean isFastqFile = (str != null && str.startsWith("@"));
		in.close();
		is.close();

		return isFastqFile;
	}

	private void readFastqFile()
			throws Exception
	{
		if (useAscii)
			in = new BufferedReader(new InputStreamReader(getInputStream(fastqIndex), "ASCII"));
		else
			in = new BufferedReader(new InputStreamReader(getInputStream(fastqIndex)));

		System.out.println("FASTQ: " + files[fastqIndex]);

		Contig contig = null;
		StringBuilder sb = null;

		StringBuilder qlt = null;

		while ((str = readLine()) != null && okToRun)
		{
			if (str.startsWith("@"))
			{
				sb = new StringBuilder();

				String name = str.substring(1, str.length());

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
					str = readLine();
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
		in.close();

		assembly.setName(files[maqIndex].getName());
	}

	private void readMaqFile()
			throws Exception
	{
		if (useAscii)
			in = new BufferedReader(new InputStreamReader(getInputStream(maqIndex), "ASCII")); // ISO8859_1
		else
			in = new BufferedReader(new InputStreamReader(getInputStream(maqIndex)));

		System.out.println("Maq:  " + files[maqIndex]);

		int readID = 0;

		while ((str = readLine()) != null && okToRun)
		{
			String[] tokens = str.split("\t");

			String name = tokens[0];
			String data = tokens[14];
			String chr  = tokens[1];
			boolean complemented = tokens[3].equals("-");
			int pos = Integer.parseInt(tokens[2]) - 1;

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
