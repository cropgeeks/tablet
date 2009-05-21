package tablet.io;

import java.io.*;
import java.lang.management.*;
import java.util.*;

import tablet.data.*;
import tablet.data.cache.*;

class AceFileReader extends AssemblyReader
{
	private boolean useAscii;
	private BufferedReader in;

	// Stores each line as it is read
	private String str;

	private Contig contig;
	private Consensus consensus;
	private Read read;

	// The index of the current read within the current contig (being read)
	private int currentReadInContig = 0;

	// We maintain a local hashtable of contigs to help with finding a
	// contig quickly when processing consensus tags
	private Hashtable<String, Contig> contigHash = new Hashtable<String, Contig>();

	AceFileReader(boolean useAscii)
	{
		this.useAscii = useAscii;
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		if (useAscii)
			in = new BufferedReader(new InputStreamReader(is, "ASCII")); // ISO8859_1
		else
			in = new BufferedReader(new InputStreamReader(is));


		// Read and check for the header
		str = in.readLine();

		if (str.startsWith("AS ") == false)
			throw new ReadException(ReadException.UNKNOWN_FORMAT);


		// Scan for contigs
		while ((str = in.readLine()) != null && okToRead)
		{
			if (str.startsWith("AF "))
				processReadLocation();

			else if (str.startsWith("RD "))
				processRead();

//			else if (str.startsWith("QA "))
//				processReadQualities();

			else if (str.startsWith("CO "))
				processContig();

			else if (str.startsWith("BQ"))
				processBaseQualities();

			else if (str.startsWith("BS "))
			{
//				processBaseSegment();
			}

			// Currently not doing anything with these tags
			else if (str.startsWith("CT{"))
				processConsesusTag();
			else if (str.startsWith("RT{"))
				while ((str = in.readLine()) != null && !str.startsWith("}"));
			else if (str.startsWith("WA{"))
				while ((str = in.readLine()) != null && !str.startsWith("}"));
		}

		in.close();
	}

	private void processContig()
		throws Exception
	{
		// CO <contig name> <# of bases> <# of reads in contig> <# of base segments in contig> <U or C>
		String[] CO = str.split("\\s+");

		String name = new String(CO[1]);
		int baseCount = Integer.parseInt(CO[2]);
		int readCount = Integer.parseInt(CO[3]);
		boolean complemented = (CO[5].charAt(0) == 'C');

		contig = new Contig(name, complemented, readCount);
		assembly.addContig(contig);
		contigHash.put(name, contig);


		// Reference sequence (immediately follows CO line)
		StringBuilder ref = new StringBuilder(baseCount);
		while ((str = in.readLine()) != null && str.length() > 0)
			ref.append(str);

		consensus = new Consensus();
		consensus.setData(ref.toString());
		contig.setConsensusSequence(consensus);

		currentReadInContig = 0;
	}

	private void processBaseQualities()
		throws Exception
	{
		// The extra " " is added to deal with files that don't have a space
		// after the final BQ score on a line, eg: "50 99\n68 45" etc
		// What we really want to read is "50 99 \n68 45"

		StringBuilder bq = new StringBuilder(consensus.length());
		while ((str = in.readLine()) != null && str.length() > 0)
			bq.append(" " + str);

		consensus.setBaseQualities(bq.toString().trim());
	}

	private void processReadLocation()
		throws Exception
	{
		// AF <read name> <C or U> <padded start consensus position>
		String[] AF = str.split("\\s+");

		String name = new String(AF[1]);
		boolean isComplemented = (AF[2].charAt(0) == 'C');
		int position = Integer.parseInt(AF[3]);

		// Store the read's name in the cache; but only store the returned
		// lookup ID for that name in the read
		ReadMetaData rmd = new ReadMetaData(name, isComplemented);
		int id = readCache.setReadMetaData(rmd);

		read = new Read(id, position-1);
		contig.getReads().add(read);

		if (id % 100000 == 0 && id != 0)
			System.out.println(" read id for contig: " + id);
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
		String[] RD = str.split("\\s+");

		int baseCount = Integer.parseInt(RD[2]);

		StringBuilder seq = new StringBuilder(baseCount);
		while ((str = in.readLine()) != null && str.length() > 0)
			seq.append(str);

		// Fetch the (expected) read for this location
		Read read = contig.getReads().get(currentReadInContig);
		read.setData(seq.toString());

		currentReadInContig++;

		if (currentReadInContig % 100000 == 0)
			System.out.println(" reads for this contig: " + currentReadInContig);
	}

	private void processReadQualities()
		throws Exception
	{
		// QA <qual clipping start> <qual clipping end> <align clipping start> <align clipping end>
		String[] QA = str.split("\\s+");

		int qa_start = Integer.parseInt(QA[1]);
		int qa_end = Integer.parseInt(QA[2]);

		int al_start = Integer.parseInt(QA[3]);
		int al_end = Integer.parseInt(QA[4]);

		read.setQAData(qa_start, qa_end, al_start, al_end);
	}

	private void processConsesusTag()
		throws Exception
	{
		// The next line of the tag should be the one with all the info on it
		str = in.readLine();
		String[] CT = str.split("\\s+");

		// POLYMORPHIC SNPs
		if (CT[1].equalsIgnoreCase("polymorphism"))
		{
			// Read the information
			String contigName = CT[0];
			int p1 = Integer.parseInt(CT[3]) - 1;
			int p2 = Integer.parseInt(CT[4]) - 1;

			// And assuming a contig exists with this name...
			Contig contig = contigHash.get(contigName);
			if (contig != null)
				contig.getFeatures().add(new Feature(Feature.SNP, p1, p2));
		}

		else if (CT[1].equalsIgnoreCase("comment") && CT[2].equalsIgnoreCase("gigaBayes"))
		{
			// Read the information
			String contigName = CT[0];
			int p1 = Integer.parseInt(CT[3]) - 1;
			int p2 = Integer.parseInt(CT[4]) - 1;

			str = in.readLine().trim();
			if (str.equalsIgnoreCase("Variation type=SNP"))
			{
				// And assuming a contig exists with this name...
				Contig contig = contigHash.get(contigName);
				if (contig != null)
					contig.getFeatures().add(new Feature(Feature.SNP, p1, p2));
			}
		}


		// Read until the end of the tag
		while ((str = in.readLine()) != null && !str.startsWith("}"));
	}

	private void processReadTag()
		throws Exception
	{
		while ((str = in.readLine()) != null && !str.startsWith("}"));
	}

	private void processAssemblyTag()
		throws Exception
	{
		while ((str = in.readLine()) != null && !str.startsWith("}"));
	}
}