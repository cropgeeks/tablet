// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.regex.*;

import static tablet.io.AssemblyFile.*;

import scri.commons.gui.*;

import net.sf.samtools.*;

class FileScanner extends SimpleJob
{
	protected AssemblyFile aFile;
	private static Pattern p = Pattern.compile("\\s+");

	File file;
	Integer type;
	Integer contigCount;
	Integer readCount;
	boolean isPaired;
	boolean isCompressed;

	FileScanner(AssemblyFile aFile, int type)
	{
		this.aFile = aFile;
		this.type = type;
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		// Determine basic file information:
		file = aFile.getFile();

		isCompressed = aFile.isCompressed();

		switch (type)
		{
			case ACE: scanACE(); break;
			case SAM: scanSAM(); break;
			case BAM: scanBAM(); break;

			case FASTA: scanFASTA(); break;
		}
	}

	void scanACE()
		throws Exception
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(
			aFile.getDataStream()));

		String str = in.readLine();
		String[] AS = p.split(str);

		contigCount = Integer.parseInt(AS[1]);
		readCount = Integer.parseInt(AS[2]);

		in.close();
	}

	void scanSAM()
		throws Exception
	{
//		readCount = FileUtils.countLines(aFile.getFile(), 4096);
	}

	void scanBAM()
		throws Exception
	{
		SAMFileReader bamReader = new SAMFileReader(aFile.getFile());
		bamReader.setValidationStringency(SAMFileReader.ValidationStringency.LENIENT);

		// Count the contigs
		contigCount = 0;
		for (SAMSequenceRecord record : bamReader.getFileHeader().getSequenceDictionary().getSequences())
			contigCount++;

		// Scan for paired end data (first 5000 records only)
		int count = 0;
		for (SAMRecord record: bamReader)
		{
			count++;

			if (record.getReadPairedFlag())
			{
				isPaired = true;
				break;
			}

			if (count > 5000 || !okToRun)
				break;
		}

		bamReader.close();
	}

	void scanFASTA()
		throws Exception
	{
/*		contigCount = 0;

		Reader in = new InputStreamReader(aFile.getDataStream());
		char[] buf = new char[4096];

		for (int num = in.read(buf); num >= 0 && okToRun; num = in.read(buf))
			for (char c: buf)
				if (c == '>')
					contigCount++;

		in.close();
*/
	}


	String getType()
	{
		switch (type)
		{
			case ACE:   return "ACE";
			case AFG:   return "AFG";
			case MAQ:   return "MAQ";
			case SAM:   return "SAM";
			case BAM:   return "BAM";
			case SOAP:  return "SOAP";
			case FASTA: return "FASTA";
			case FASTQ: return "FASTQ";
			case GFF3:  return "GFF3";

			default: return null;
		}
	}
}