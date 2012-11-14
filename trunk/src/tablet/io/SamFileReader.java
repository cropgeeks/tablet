// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.data.cache.*;
import tablet.gui.*;

import scri.commons.gui.*;

import net.sf.samtools.*;
import static net.sf.samtools.SAMReadGroupRecord.*;

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

	private boolean refLengthsOK = true;

	private boolean indexingCache = false;

	// Stores a list of @RG (read group) records as they are found
	private ArrayList<ReadGroup> readGroups = new ArrayList<ReadGroup>();
	// Stores a hash of @RG (read group) IDs and their associated information
	private HashMap<String, Short> rgHash = new HashMap<String, Short>();

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

		readID = 0;

		while ((str = readLine()) != null && okToRun)
		{
			// Parse outread groups from header
			if (str.startsWith("@RG"))
			{
				parseReadGroupHeader();
				continue;
			}

			else if (str.startsWith("@SQ"))
			{
				parseSequenceHeader();
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

					cigarParser = new CigarParser(contig);
				}

				if (isDummyFeature(data, flags) == false)
				{
					Read read;

					boolean isPaired = (flags & 0x0001) == 1 ? true : false;

					ReadNameData rnd = new ReadNameData(name);
					ReadMetaData rmd = new ReadMetaData(complemented);

					rmd.setIsPaired(isPaired);

					// Determine the read group this read belongs to (if any)
					determineReadGroup(tokens, rmd);

					// If paired flag is set setup the pair info
					if(isPaired)
						read = setupPairInfo(pos, mPos, rnd, iSize, flags, mrnm, chr, rmd);
					else
						read = new Read(readID, pos);

					contig.getReads().add(read);

					String bases = cigarParser.parse(data.toString(), pos, cigar, read);
					StringBuilder fullRead = new StringBuilder(bases);

					rmd.setData(fullRead);

					rnd.setUnpaddedLength(rmd.calculateUnpaddedLength());
					rnd.setCigar(cigar);
					nameCache.setReadNameData(rnd, contig);

					read.setLength(rmd.length());

					// Do base-position comparison...
					BasePositionComparator.compare(contig, rmd, read.s());

					readCache.setReadMetaData(rmd);

					readID++;
				}
				else
					createDummyFeature(tokens, pos, cigar, contig);
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
		pr.setIsMateContig(mrnm.equals("="));

		// Parse properly paired, number in pair and mate mapped out from flag field
		rnd.setIsProperPair((flags & 0x0002) != 0);
		rmd.setNumberInPair((flags & 0x0040) != 0 ? (byte)1 : 2);
		rmd.setMateMapped((flags & 0x0008) != 0 ? false : true);
		rmd.setIsMateContig(mrnm.equals(chr));

		Assembly.setIsPaired(true);

		return pr;
	}

	// Parse out the read group information from @RG lines in the header
	private void parseReadGroupHeader()
	{
		String [] tokens = str.split("\t");
		String id = tokens[1].split(":")[1];

		if (rgHash.containsKey(id) == false)
		{
			SAMReadGroupRecord record = new SAMReadGroupRecord(id);

			for (String tag: tokens)
			{
				if (tag.startsWith("CN:"))
					record.setAttribute(SEQUENCING_CENTER_TAG, tag.substring(3));
				else if (tag.startsWith("DS:"))
					record.setAttribute(DESCRIPTION_TAG, tag.substring(3));
				else if (tag.startsWith("DT:"))
					record.setAttribute(DATE_RUN_PRODUCED_TAG, tag.substring(3));
				else if (tag.startsWith("FO:"))
					record.setAttribute(FLOW_ORDER_TAG, tag.substring(3));
				else if (tag.startsWith("KS:"))
					record.setAttribute(KEY_SEQUENCE_TAG, tag.substring(3));
				else if (tag.startsWith("LB:"))
					record.setAttribute(LIBRARY_TAG, tag.substring(3));
				else if (tag.startsWith("PI:"))
					record.setAttribute(PREDICTED_MEDIAN_INSERT_SIZE_TAG, tag.substring(3));
				else if (tag.startsWith("PL:"))
					record.setAttribute(PLATFORM_TAG, tag.substring(3));
				else if (tag.startsWith("PU:"))
					record.setAttribute(PLATFORM_UNIT_TAG, tag.substring(3));
				else if (tag.startsWith("SM:"))
					record.setAttribute(READ_GROUP_SAMPLE_TAG, tag.substring(3));
			}

			readGroups.add(new ReadGroup(record));
			// Note we put the FIRST read group in as index 1 (not 0)
			rgHash.put(record.getId(), (short)readGroups.size());
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

		try
		{
			// TODO: Mapping quality can start with a string with RG: !!!
			for (int i=tagStart; i < tokens.length; i++)
				if (tokens[i].startsWith("RG:"))
					readGroupID = tokens[i].split(":")[2];

			// If a read group id was found...
			if (!readGroupID.isEmpty())
			{
				if (rgHash.containsKey(readGroupID))
					rmd.setReadGroup(rgHash.get(readGroupID));
			}

		}
		catch (Exception e) {}
	}

	private void parseSequenceHeader()
	{
		try
		{
			String [] tokens = str.split("\t");

			String name = null, md5sum = null;
			int length = 0;

			for (String token: tokens)
			{
				if (token.startsWith("SN:"))
					name = token.substring(3);

				else if (token.startsWith("LN:"))
					length = Integer.parseInt(token.substring(3));

				else if (token.startsWith("M5:"))
					md5sum = token.substring(3);
			}

			// If we found a match, check that its length matches what was found
			// in the reference file too
			if (name != null)
			{
				Contig contig = contigHash.get(name);
				if (contig != null)
				{
					int cLength = contig.getConsensus().length();

					if (length != contig.getConsensus().length())
					{
						System.out.println("Contig " + contig.getName()
							+ " lengths do not match: " + cLength + " (ref "
							+ "file), " + length + " (BAM file)");

						refLengthsOK = false;
					}
				}
			}

			if (md5sum != null)
			{
				// TODO: Decide if MD5sum from sam/bam file matches that of the
				// actual reference sequence we have stored (if any)
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void processCigarFeatures(CigarParser parser)
		throws Exception
	{
		for (HashMap<String, CigarFeature> map : parser.getFeatureMaps())
			processFeaturesForMap(map, parser);

		for (Contig contig: assembly)
			Collections.sort(contig.getFeatures());
	}

	private void processFeaturesForMap(HashMap<String, CigarFeature> map, CigarParser parser)
	{
		for (String feature : map.keySet())
		{
			String[] featureElements = feature.split("Tablet-Separator");
			CigarFeature cigarFeature = map.get(feature);

			Contig contig = contigHash.get(featureElements[0]);
			if (contig != null)
			{
				// Only add cigar features with more than a required number
				// of inserts associated with them
				if (cigarFeature.getCount() >= Prefs.visCigarInsertMinimum)
					if (contig.addFeature(cigarFeature))
						cigarFeature.verifyType();
			}
		}
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

	protected boolean refLengthsOK()
		{ return refLengthsOK; }

	private void createDummyFeature(String[] tokens, int pos, String cigar, Contig contig)
	{
		if (tokens.length > 11)
		{
			String tagString = tokens[11];
			String[] tags = tagString.split("\t");
			String ct = null;
			for (String s : tags)
				if (s.startsWith("CT"))
					ct = s;

			if (ct != null)
			{
				String[] tagElems = ct.split(";");
				Feature feature = new Feature(tagElems[1], tagElems[1], pos, pos + cigarParser.calculateLength(cigar));
				feature.setTags(Arrays.copyOfRange(tagElems, 2, tagElems.length));
				contig.addFeature(feature);
				feature.verifyType();
			}
		}
	}

	private boolean isDummyFeature(String data, int flags)
	{
		return data.equals("*") && (flags & 0x0100) != 0 && (flags & 0x0200) != 0;
	}
}