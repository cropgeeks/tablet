// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
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
	private ReadSQLCache nameCache;

	private ReferenceFileReader refReader;
	private CigarParser cigarParser;

	private HashMap<String, Contig> contigHash = new HashMap<String, Contig>();

	private int readID = 0;

	SamFileReader()
	{
	}

	SamFileReader(IReadCache readCache, ReadSQLCache nameCache)
	{
		this.readCache = readCache;
		this.nameCache = nameCache;
	}

	private void readReferenceFile()
		throws Exception
	{
		if (files.length > 1 && files[REFINDEX].isReferenceFile())
		{
			refReader = new ReferenceFileReader(assembly, contigHash);

			in = new BufferedReader(new InputStreamReader(getInputStream(REFINDEX), "ASCII"));

			refReader.readReferenceFile(this, files[1]);

			in.close();
		}
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		Assembly.setIsPaired(false);
		
		// Try and read the reference file (if there is one)
		readReferenceFile();

		in = new BufferedReader(new InputStreamReader(getInputStream(ASBINDEX), "ASCII"));

		cigarParser = new CigarParser();

		readID = 0;

		Contig prev = null;

		while ((str = readLine()) != null && okToRun)
		{
			if (str.startsWith("@"))
				continue;

			String[] tokens = str.split("\t");

			String name = tokens[0];
			String data = tokens[9];
			String cigar = tokens[5];
			String chr  = tokens[2];
			int pos = Integer.parseInt(tokens[3]) - 1;
			String mrnm = tokens[6];
			int mPos = Integer.parseInt(tokens[7]);
			int iSize = Integer.parseInt(tokens[8]);

			// Decode the U/C information from the flag field
			boolean complemented = false;
			int flags = Integer.parseInt(tokens[1]);
			if ((flags & 0x0010) != 0)
				complemented = true;

			// TODO: Unmapped reads?
			if ((flags & 0x0004) != 0)
				continue;

			Contig contigToAddTo = contigHash.get(chr);

			// If it wasn't found (and we don't have ref data), make it
			if (contigToAddTo == null && refReader == null)
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

				Read read;


				ReadNameData rnd = new ReadNameData(name);
				ReadMetaData rmd = new ReadMetaData(complemented);

				if((flags & 0x0001) != 0)
				{
					MatedRead pr = new MatedRead(readID, pos);
					pr.setMatePos(mPos);
					read = pr;
					rnd.setInsertSize(iSize);
					rnd.setIsProperPair((flags & 0x0002) != 0);
					rnd.setNumberInPair((flags & 0x0040) != 0 ? 1 : 2);
					rnd.setMateContig(mrnm);

					rmd.setNumberInPair((flags & 0x0040) != 0 ? 1 : 2);

					// Might want to get rid of this
					if(!Assembly.isPaired())
						Assembly.setIsPaired(true);

					boolean isMateContig = mrnm.equals(chr);
					pr.setIsMateContig(isMateContig);
				}
				else
					read = new Read(readID, pos);

				contigToAddTo.getReads().add(read);

				StringBuilder fullRead = new StringBuilder(cigarParser.parse(
					data.toString(), pos, cigar));

				rmd.setData(fullRead);

				int uLength = rmd.calculateUnpaddedLength();
				rnd.setUnpaddedLength(uLength);
				rnd.setCigar(cigar);
				nameCache.setReadNameData(rnd);

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

		assembly.setName(files[ASBINDEX].getName());
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
			Contig contig = contigHash.get(featureElements[0]);
			if (contig != null)
			{
				contig.addFeature(cigarFeature);
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