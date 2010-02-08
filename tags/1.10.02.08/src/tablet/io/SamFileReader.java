// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.cache.*;

import scri.commons.gui.*;
import tablet.data.auxiliary.CigarFeature;

class SamFileReader extends TrackableReader
{
	private IReadCache readCache;

	private ReferenceFileReader refReader;
	private CigarParser cigarParser;

	// The index of the SAM file in the files[] array
	private int samIndex = -1;
	// The index of the reference file in the files[] array
	private int refIndex = -1;

	private HashMap<String, Contig> contigHash = new HashMap<String, Contig>();

	private int readID = 0;

	SamFileReader()
	{
	}

	SamFileReader(IReadCache readCache)
	{
		this.readCache = readCache;
	}

	boolean canRead()
		throws Exception
	{
		refReader = new ReferenceFileReader(assembly, contigHash);

		// We need to check each file to see if it is readable
		for (int i = 0; i < files.length; i++)
		{
			if (isSamFile(i))
				samIndex = i;

			else if (refReader.canRead(files[i]) != AssemblyFileHandler.UNKNOWN)
				refIndex = i;
		}

		return (samIndex >= 0);
	}

	// Checks to see if this is a SAM file by assuming 11 columns of \t data and
	// a second column containing an integer (crude I know!)
	private boolean isSamFile(int fileIndex)
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(fileIndex, true)));
		str = readLine();

		// Does it start with an @HD header line
		boolean isSamFile = false;

		// Check the header (but @ might match on a FASTQ file too)
		if (str != null && str.startsWith("@HD"))
			// Keep reading past the header
			while ((str = readLine()) != null && str.length() > 0 && str.startsWith("@"));

		String[] tokens = str.split("\t");
		if (tokens.length >= 11)
		{
			// The 2nd column should be a number
			try	{
				Integer.parseInt(tokens[1]);
				isSamFile = true;
			}
			catch (Exception e) {}
		}

		in.close();
		is.close();

		return isSamFile;
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		// Read reference information (if it exists)
		if (refIndex >= 0)
			readReferenceFile();

		// Then read the main assembly/read data file
		readSamFile();
	}

	private void readReferenceFile()
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(refIndex, true), "ASCII"));

		refReader.readReferenceFile(this, files[refIndex]);

		in.close();
	}

	private void readSamFile()
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(samIndex, true), "ASCII"));

		cigarParser = new CigarParser();

		readID = 0;

		Contig prev = null;

		while ((str = readLine()) != null && okToRun)
		{
			if (str.startsWith("@"))
				continue;

			String[] tokens = str.split("\t");

			String name = new String(tokens[0]);
			String data = new String(tokens[9]);
			String cigar = new String(tokens[5]);
			String chr  = new String(tokens[2]);
			Integer pos = Integer.parseInt(tokens[3]) - 1;

			// Decode the U/C information from the flag field
			boolean complemented = false;
			int flags = Integer.parseInt(tokens[1]);
			if ((flags & 0x0010) != 0)
				complemented = true;

			// TODO: Unmapped reads?
//			if ((flags & 0x0004) != 0)
//			{

//			}

//			Read read = new Read(readID, pos);

			Contig contigToAddTo = contigHash.get(chr);

			// If it wasn't found (and we don't have ref data), make it
			if (contigToAddTo == null && refIndex == -1)
			{
				contigToAddTo = new Contig(chr);
				contigHash.put(chr, contigToAddTo);

				assembly.addContig(contigToAddTo);
			}

			if (contigToAddTo != null)
			{
				if(prev == null)
				{
					prev = contigToAddTo;
					cigarParser.setCurrentContigName(contigToAddTo.getName());
				}
				else if(prev != contigToAddTo)
				{
					prev = contigToAddTo;
					cigarParser.setCurrentContigName(contigToAddTo.getName());
				}

				Read read = new Read(readID, pos);

				contigToAddTo.getReads().add(read);

				String fullRead = cigarParser.parse(
					data.toString(), pos, cigar);

				ReadMetaData rmd = new ReadMetaData(name, complemented);
				rmd.setData(fullRead);
				rmd.calculateUnpaddedLength();
				rmd.setCigar(cigar);
				read.setLength(rmd.length());

				// Do base-position comparison...
				BasePositionComparator.compare(contigToAddTo, rmd,
					read.getStartPosition());

				readCache.setReadMetaData(rmd);

				readID++;
			}
		}

		in.close();

		processCigarFeatures(cigarParser);

		assembly.setName(files[samIndex].getName());
		assembly.setHasCigar();
	}

	private void processCigarFeatures(CigarParser parser)
		throws Exception
	{
		for (String feature : parser.getFeatureMap().keySet())
		{
			String[] featureElements = feature.split("Tablet-Separator");
			int count = parser.getFeatureMap().get(feature);
			CigarFeature cigarFeature = new CigarFeature("CIGAR-I", "",
				Integer.parseInt(featureElements[1]) - 1, Integer.parseInt(featureElements[1]), count);
			Contig ctg = contigHash.get(featureElements[0]);
			if (ctg != null)
			{
				ctg.getFeatures().add(cigarFeature);
			}
		}

		for (Contig contig: assembly)
			Collections.sort(contig.getFeatures());
	}

	public String getMessage()
	{
		return RB.format("io.AssemblyFileHandler.status",
			getTransferRate(), contigHash.size(), readID);
	}
}