package av.io;

import java.io.*;
import java.util.*;

import av.data.*;

public class AceFileReader
{
	private boolean useAscii;
	private InputStream is;
	private BufferedReader in;

	// Stores each line as it is read
	private String str;

	private int expectedReads = 0;

	// Data structures used as the file is read
	private DNATable dnaTable = new DNATable();

	private Contig contig;
	private Consensus consensus;
	private Read read;

	AceFileReader(InputStream is, boolean useAscii)
		throws Exception
	{
		this.is = is;
		this.useAscii = useAscii;
	}

	void read()
		throws Exception
	{
		if (useAscii)
			in = new BufferedReader(new InputStreamReader(is, "ASCII"));	// ISO8859_1
		else
			in = new BufferedReader(new InputStreamReader(is));

		int lines = 0;
		long s = System.currentTimeMillis();

		// Scan for contigs
		while ((str = in.readLine()) != null)
		{
			if (str.startsWith("AF "))
				processReadLocation();

			else if (str.startsWith("RD "))
				processRead();

			else if (str.startsWith("CO "))
				processContig();

			else if (str.startsWith("BQ"))
				processBaseQualities();

			else if (str.startsWith("AS "))
				expectedReads = Integer.parseInt(str.split(" ")[2]);

			else if (str.startsWith("BS "))
				processBaseSegment();

			// Currently not doing anything with these tags
			else if (str.startsWith("CT{"))
				while ((str = in.readLine()) != null && !str.startsWith("}"));
			else if (str.startsWith("RT{"))
				while ((str = in.readLine()) != null && !str.startsWith("}"));
			else if (str.startsWith("WA{"))
				while ((str = in.readLine()) != null && !str.startsWith("}"));

			if (++lines % 1000000 == 0)
			{
				long e = System.currentTimeMillis();
				System.out.println(lines + " (" + (e-s) + "ms)");
				s = System.currentTimeMillis();
			}

		}

		System.out.println("final read: " + read.getName());
		System.out.println("final vect: " + contig.getReads().get(contig.getReads().size()-1).getName());

		in.close();


		// Post file-read processing

//		// TODO: Needs to be done on all contigs
//		Collections.sort(contig.getReads());
	}

	private void processContig()
		throws Exception
	{
		// CO <contig name> <# of bases> <# of reads in contig> <# of base segments in contig> <U or C>
		String[] CO = str.split(" ");

		String name = new String(CO[1]);
		int baseCount = Integer.parseInt(CO[2]);
		int readCount = Integer.parseInt(CO[3]);
		boolean complemented = (CO[5].charAt(0) == 'C');

		contig = new Contig(name, complemented, readCount);
		System.out.println("CONTIG: " + name);


		// Reference sequence (immediately follows CO line)
		StringBuilder ref = new StringBuilder(baseCount);
		while ((str = in.readLine()) != null && str.length() > 0)
			ref.append(str);

		consensus = new Consensus();
		long s = System.currentTimeMillis();
		consensus.setData(ref.toString());
		long e = System.currentTimeMillis();

		System.out.println("con data in " + (e-s));
	}

	private void processBaseQualities()
		throws Exception
	{
		StringBuilder bq = new StringBuilder(consensus.length());
		while ((str = in.readLine()) != null && str.length() > 0)
			bq.append(str);

		consensus.setBaseQualities(bq.toString().trim());
	}

	private void processReadLocation()
		throws Exception
	{
		// AF <read name> <C or U> <padded start consensus position>
		String[] AF = str.split(" ");

		String name = new String(AF[1]);
		boolean complemented = (AF[2].charAt(0) == 'C');
		int position = Integer.parseInt(AF[3]);

		read = new Read(name, complemented, position);
		contig.getReads().add(read);

//		System.out.println("Read: " + read.getName() + " - " + read.getComplemented() + " - " + read.getPosition());
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
	}

	private void processRead()
		throws Exception
	{
		// RD <read name> <# of padded bases> <# of whole read info items> <# of read tags>
		String[] RD = str.split(" ");

		int baseCount = Integer.parseInt(RD[2]);

		StringBuilder seq = new StringBuilder(baseCount);
		while ((str = in.readLine()) != null && str.length() > 0)
			seq.append(str);

		read.setData(seq.toString());
	}

	private void processConsesusTag()
		throws Exception
	{
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