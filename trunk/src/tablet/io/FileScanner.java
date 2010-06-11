// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;

import static tablet.io.AssemblyFileHandler.*;

import net.sf.samtools.*;
import net.sf.samtools.util.*;

class FileScanner
{
	protected AssemblyFile aFile;

	File file;
	int type;
	boolean isPaired;

	FileScanner(AssemblyFile aFile, int type)
	{
		this.aFile = aFile;
		this.type = type;
	}

	void scan()
		throws Exception
	{
		// Determine basic file information:
		file = aFile.getFile();

		switch (type)
		{
			case BAM: scanBAM(); break;
		}
	}

	void scanBAM()
		throws Exception
	{
		SAMFileReader bamReader = new SAMFileReader(aFile.getFile());
		bamReader.setValidationStringency(SAMFileReader.ValidationStringency.LENIENT);

		int count = 0;

		for (SAMRecord record: bamReader)
		{
			count++;

			if (record.getReadPairedFlag())
			{
				isPaired = true;
				break;
			}

			if (count > 50000)
				break;
		}

		bamReader.close();
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

			default: return null;
		}
	}
}