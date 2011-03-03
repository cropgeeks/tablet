// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.cache.*;
import tablet.data.auxiliary.*;
import static tablet.io.ReadException.*;

import scri.commons.gui.*;

public class AceFileReader extends TrackableReader
{
	public static boolean PROCESS_QA = true;

	private IReadCache readCache;
	private ReadSQLCache nameCache;

	private Contig contig;
	private Consensus consensus;
	private Read read;

	// Index trackers for counting AF and RD lines as they are parsed
	private int afIndex = 0, rdIndex = 0;

	// Temp storage of U/C data until it can be added to the cache
	private boolean[] ucCache;

	// Incrementing count of the number of reads processed
	private int readsFound = 0;

	// Count of the number of reads and contigs processed (GUI tracking only)
	private int contigsAdded = 0;
	private int readsAdded = 0;

	// We maintain a local hashtable of contigs to help with finding a
	// contig quickly when processing consensus tags
	private HashMap<String, Contig> contigHash = new HashMap<String, Contig>();

	// Pretty much all the tokenizing we do is based on this one pattern
	private Pattern p = Pattern.compile("\\s+");

	// Collection of read related variables that are filled by various methods
	// as we attempt to process a read
	private String[] RD;
	private StringBuilder seq;
	private int qa_start, qa_end;


	AceFileReader()
	{
	}

	AceFileReader(IReadCache readCache, ReadSQLCache nameCache)
	{
		this.readCache = readCache;
		this.nameCache = nameCache;
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(ASBINDEX), "ASCII"));

		// Read in the header
		str = readLine();

		String[] AS = p.split(str);

		Assembly.setIsPaired(false);
		// Initialize the vector of contigs to be at least this size
		assembly.setContigsSize(Integer.parseInt(AS[1]));

		// Scan for contigs
		while ((str = readLine()) != null && okToRun)
		{
			if (str.startsWith("AF "))
				processReadLocation();

			else if (str.startsWith("RD "))
				processRead();

			else if (str.startsWith("QA "))
				processReadQualities();

			else if (str.startsWith("CO "))
				processContig();

			else if (str.startsWith("BQ"))
				processBaseQualities();

//			else if (str.startsWith("BS "))
//			{
//				processBaseSegment();
//			}

			// Currently not doing anything with these tags
			else if (str.startsWith("CT{"))
				while ((str = readLine()) != null && str.length() > 0);
			else if (str.startsWith("RT{"))
				while ((str = readLine()) != null && str.length() > 0);
			else if (str.startsWith("WA{"))
				while ((str = readLine()) != null && str.length() > 0);
		}

		assembly.setName(files[ASBINDEX].getName());


		// Remove any reads that got marked as null due to being poor quality
		if (PROCESS_QA)
		{
			for (Contig contig: assembly)
			{
				ArrayList<Read> reads = contig.getReads();
				for (int i = reads.size()-1; i >= 0; i--)
					if (reads.get(i) == null)
						reads.remove(i);
			}
		}
	}

	private void processContig()
		throws Exception
	{
		// CO <contig name> <# of bases> <# of reads in contig> <# of base segments in contig> <U or C>
		String[] CO = p.split(str);

		if (CO.length != 6)
			throw new ReadException(currentFile(), lineCount, TOKEN_COUNT_WRONG);

		String name = new String(CO[1]);
		int baseCount = Integer.parseInt(CO[2]);
		int readCount = Integer.parseInt(CO[3]);
		boolean complemented = (CO[5].charAt(0) == 'C');

		contig = new Contig(name, complemented, readCount);
		assembly.addContig(contig);
		contigHash.put(name, contig);

		// Reference sequence (immediately follows CO line)
		StringBuilder ref = new StringBuilder(baseCount);
		while ((str = readLine()) != null && str.length() > 0)
			ref.append(str);

		consensus = new Consensus();
		consensus.setData(ref);
		consensus.calculateUnpaddedLength();
		contig.setConsensusSequence(consensus);

		ucCache = new boolean[readCount];

		afIndex = 0;
		rdIndex = 0;

		contigsAdded++;
	}

	private void processBaseQualities()
		throws Exception
	{
		// The extra " " is added to deal with files that don't have a space
		// after the final BQ score on a line, eg: "50 99\n68 45" etc
		// What we really want to read is "50 99 \n68 45"

//		StringBuilder bqStr = new StringBuilder(consensus.length());
		StringBuilder bqStr = null;
		while ((str = readLine()) != null && str.length() > 0);
//			bqStr.append(" " + str);

		if (true)
			return;

		String[] tokens = p.split(bqStr.toString().trim());
		byte[] bq = new byte[consensus.length()];

		// Deal with (ignore) **** in consensus sequence and no base qualities
		if (tokens.length == 1 && tokens[0].length() == 0)
			return;

		int uLength = consensus.getUnpaddedLength();
		if (tokens.length != uLength)
			throw new Exception("Expected " + uLength + " base qualities but "
				+ "found " + tokens.length + " (contig '" + contig.getName()
				+ "' with length " + consensus.length() + ")");

		for (int t = 0, i = 0; t < uLength; t++, i++)
		{
			// Skip padded bases, because the quality string doesn't score them
			while (consensus.getStateAt(i) == Sequence.P)
			{
				bq[i] = -1;
				i++;
			}

			bq[i] = Byte.parseByte(tokens[t]);

			if (bq[i] > 100)
				bq[i] = 100;
		}

		consensus.setBaseQualities(bq);
	}

	private void processReadLocation()
		throws Exception
	{
		// AF <read name> <C or U> <padded start consensus position>
		String[] AF = p.split(str);

		if (AF.length != 4)
			throw new ReadException(currentFile(), lineCount, TOKEN_COUNT_WRONG);

		boolean isComplemented = (AF[2].charAt(0) == 'C');
		int position = Integer.parseInt(AF[3]);

		ucCache[afIndex++] = isComplemented;

		read = new Read(readsFound, position-1);
		contig.getReads().add(read);

		readsFound++;
	}

	private void processBaseSegment()
		throws Exception
	{
		// BS <padded start consensus position> <padded end consensus position> <read name>

		// eg: BS 516 516 K26-217c

		// Could the two numbers be stored in the Read rather than in the consensus?
		// It'll depend on WHY this information is actually needed. It'll certainly
		// use less memory storing it in the actual read, but processing will be
		// slow as we'll have to FIND the read first - a hashtable would mean
		// instant lookup, but would use a ton of memory during loading
		//  (thinking about the 6M and 19M read data files)

		// If held in the consensus...HOW? hashtable or array? either will require
		// a lot of memory for pointers to the actual reads

		// 21/04/2009 - array of integers, one int per base of the consensus
		// sequence. each int is the cache ID of the read that is best for that
		// position. Consensus sequence isn't very long so memory less of an issue
	}

	private void processRead()
		throws Exception
	{
		// RD <read name> <# of padded bases> <# of whole read info items> <# of read tags>
		RD = p.split(str);

		if (RD.length != 5)
			throw new ReadException(currentFile(), lineCount, TOKEN_COUNT_WRONG);

		int baseCount = Integer.parseInt(RD[2]);

		// Read the data
		seq = new StringBuilder(baseCount);
		while ((str = readLine()) != null && str.length() > 0)
			seq.append(str);
	}

	private void processReadQualities()
		throws Exception
	{
		// QA <qual clipping start> <qual clipping end> <align clipping start> <align clipping end>
		String[] QA = p.split(str);

		qa_start = Integer.parseInt(QA[1]) - 1;
		qa_end = Integer.parseInt(QA[2]) - 1;

//		int al_start = Integer.parseInt(QA[3]);
//		int al_end = Integer.parseInt(QA[4]);

		addRead();
	}

	// Adds a read, once all info on it has been gathered
	private void addRead()
		throws Exception
	{
		// Fetch the read for this location
		read = contig.getReads().get(rdIndex);

		if (PROCESS_QA)
		{
			// Totally crap read has QA values of -1 (or -2 as seen by Tablet)
			if (qa_start != -2 && qa_end != -2)
			{
				seq = new StringBuilder(seq.substring(qa_start, qa_end+1));
				read.setStartPosition(read.getStartPosition() + qa_start);
			}
			else
				contig.getReads().set(rdIndex, null);
		}

		ReadNameData rnd = new ReadNameData(RD[1]);

		// Store the metadata about the read in the cache
		ReadMetaData rmd = new ReadMetaData(ucCache[rdIndex]);
		rmd.setData(seq);

		int uLength = rmd.calculateUnpaddedLength();
		rnd.setUnpaddedLength(uLength);
		nameCache.setReadNameData(rnd);
		
		read.setLength(rmd.length());

		// Do base-position comparison...
		BasePositionComparator.compare(contig, rmd,
			read.getStartPosition());

		readCache.setReadMetaData(rmd);

		rdIndex++;
		readsAdded++;
	}

	public String getMessage()
	{
		return RB.format("io.AssemblyFileHandler.status",
			getTransferRate(), contigsAdded, readsAdded);
	}
}