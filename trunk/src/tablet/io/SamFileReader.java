// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.data.cache.*;

import scri.commons.gui.*;

class SamFileReader extends TrackableReader
{
	private IReadCache readCache;
	private ReadSQLCache nameCache;

	private ReferenceFileReader refReader;
	private CigarParser cigarParser;

	private HashMap<String, Contig> contigHash = new HashMap<String, Contig>();

	// Contig currently being processed. Used to detect unsorted data.
	private String currentContig = "";
	private boolean isUnsorted = false;

	private int readID = 0;

	private boolean indexingCache = false;

	private ArrayList<String> readGroups = new ArrayList<String>();
	private HashMap<String, Short> sampleHash = new HashMap<String, Short>();
	private HashMap<String, String> readGroupIDHash = new HashMap<String, String>();


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

	@Override
	public void runJob(int jobIndex)
		throws Exception
	{
		Assembly.setIsPaired(false);

		// Try and read the reference file (if there is one)
		readReferenceFile();

		in = new BufferedReader(new InputStreamReader(getInputStream(ASBINDEX), "ASCII"));

		cigarParser = new CigarParser();

		readID = 0;

		while ((str = readLine()) != null && okToRun)
		{
			// Parse outread groups from header
			if (str.startsWith("@RG"))
			{
				parseReadGroupHeader();
				continue;
			}

			else if (str.startsWith("@"))
				continue;

			String[] tokens = str.split("\t");

			String name = tokens[0];
			String data = tokens[9];
			String cigar = tokens[5];
			String chr  = tokens[2];
			int pos = Integer.parseInt(tokens[3]) - 1;
			String mrnm = tokens[6];
			// Working fix for problem Micha noted
			int mPos, iSize;

			try
			{
				mPos = Integer.parseInt(tokens[7]) - 1;
			}
			catch(NumberFormatException e)
			{
				mPos = 0;
			}

			try
			{
				iSize = Integer.parseInt(tokens[8]);
			}
			catch(NumberFormatException e)
			{
				iSize = 0;
			}

			// Decode the U/C information from the flag field
			boolean complemented = false;
			int flags = Integer.parseInt(tokens[1]);
			if ((flags & 0x0010) != 0)
				complemented = true;

			// TODO: Unmapped reads?
			if ((flags & 0x0004) != 0)
				continue;

			Contig contig = contigHash.get(chr);

			// If it wasn't found (and we don't have ref data), make it
			if (contig == null && refReader == null)
			{
				contig = new Contig(chr);
				contigHash.put(chr, contig);

				assembly.addContig(contig);
			}

			if (contig != null)
			{
				// Unsorted check...
				if (contig.getName().equals(currentContig) == false)
				{
					currentContig = contig.getName();

					if (contig.getReads().size() > 0)
						isUnsorted = true;
				}

				cigarParser.setCurrentContigName(contig.getName());

				Read read;

				ReadNameData rnd = new ReadNameData(name);
				ReadMetaData rmd = new ReadMetaData(complemented);

				rmd.setIsPaired((flags & 0x0001) == 1 ? true : false);

				// Determine the read group this read belongs to (if any)
				determineReadGroup(tokens, rmd);

				// If paired flag is set setup the pair info
				if((flags & 0x0001) != 0)
					read = setupPairInfo(pos, mPos, rnd, iSize, flags, mrnm, chr, rmd);
				else
					read = new Read(readID, pos);

				contig.getReads().add(read);

				StringBuilder fullRead = new StringBuilder(cigarParser.parse(
					data.toString(), pos, cigar, read));

				rmd.setData(fullRead);

				rnd.setUnpaddedLength(rmd.calculateUnpaddedLength());
				rnd.setCigar(cigar);
				nameCache.setReadNameData(rnd, contig);

				read.setLength(rmd.length());

				// Do base-position comparison...
				BasePositionComparator.compare(contig, rmd,
					read.getStartPosition());

				readCache.setReadMetaData(rmd);

				readID++;
			}
		}

		in.close();

		processCigarFeatures(cigarParser);

		assembly.setName(files[ASBINDEX].getName());
		assembly.setHasCigar();

		if (Assembly.isPaired())
		{
			setIndexingCache();
			nameCache.indexNames();

			if (isUnsorted)
			{
				TaskDialog.warning(
					RB.getString("io.SamFileReader.unsortedError"),
					RB.getString("gui.text.close"));
			}
		}

		assembly.setReadGroups(readGroups);
	}

	// Setup paired read information when we have a paired read
	private MatedRead setupPairInfo(int pos, int mPos, ReadNameData rnd, int iSize,
		int flags, String mrnm, String chr, ReadMetaData rmd)
	{
		MatedRead pr = new MatedRead(readID, pos);
		pr.setMatePos(mPos);

		rnd.setInsertSize(iSize);
		// If mate is in same contig its reference can be set as = instead of contig name
		rnd.setMateContig(mrnm.equals("=") ? mrnm : chr);
		// If mate reference name equals contig name its mate is in the same contig
		pr.setIsMateContig(mrnm.equals(chr));

		// Parse properly paired, number in pair and mate mapped out from flag field
		rnd.setIsProperPair((flags & 0x0002) != 0);
		rmd.setNumberInPair((flags & 0x0040) != 0 ? 1 : 2);
		rmd.setMateMapped((flags & 0x0008) != 0 ? false : true);

		Assembly.setIsPaired(true);

		return pr;
	}

	// Parse out the read group information from @RG lines in the header
	private void parseReadGroupHeader()
	{
		String [] tokens = str.split("\t");
		String id = tokens[1].split(":")[1];

		if (readGroupIDHash.containsKey(id) == false)
		{
			String sample = tokens[2].split(":")[1];
			readGroupIDHash.put(id, sample);

			if (readGroups.contains(sample) == false)
			{
				readGroups.add(sample);
				// Note we put the FIRST read group in as index 1 (not 0)
				sampleHash.put(sample, (short)(readGroups.size()));
			}
		}
	}

	// Determine read group and set read group field in ReadMetaData
	private void determineReadGroup(String[] tokens, ReadMetaData rmd)
	{
		int tagStart = 10;

		// Loop over all the tags in the read
		if (tokens.length < tagStart)
			return;

		String readGroupID = "";

		for (int i=tagStart; i < tokens.length; i++)
			if (tokens[i].startsWith("RG"))
				readGroupID = tokens[i].split(":")[2];

		// If a read group id was found, get the sample number and set it in rmd
		if (!readGroupID.isEmpty())
		{
			String sample = readGroupIDHash.get(readGroupID);
			// If there are no @RG lines in header sample will be null
			if (sample != null)
				rmd.setReadGroup(sampleHash.get(sample));
		}
	}

	private void processCigarFeatures(CigarParser parser)
		throws Exception
	{
		for (String feature : parser.getFeatureMap().keySet())
		{
			String[] featureElements = feature.split("Tablet-Separator");
			CigarFeature cigarFeature = parser.getFeatureMap().get(feature);

			Contig contig = contigHash.get(featureElements[0]);
			if (contig != null)
			{
				if (contig.addFeature(cigarFeature))
					cigarFeature.verifyType();
			}
		}

		for (Contig contig: assembly)
			Collections.sort(contig.getFeatures());
	}

	@Override
	public String getMessage()
	{
		if (!indexingCache)
			return RB.format("io.AssemblyFileHandler.status",
				getTransferRate(), contigHash.size(), readID);
		else
			return RB.getString("io.AssemblyFileHandler.status.indexingCache");

	}

	private void setIndexingCache()
	{
		isIndeterminate = true;
		indexingCache = true;
	}
}