// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io;

import java.util.*;

import tablet.analysis.*;
import tablet.data.cache.*;
import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.*;
import tablet.io.samtools.*;

import net.sf.samtools.*;

import scri.commons.gui.*;

public class BamFileHandler
{
	public static boolean VALIDATION_LENIENT = false;

	private IReadCache readCache;
	private ReadSQLCache nameCache;
	private AssemblyFile bamFile, baiFile;
	private SAMFileReader bamReader;
	private Assembly assembly;
	private int readID;

	private boolean okToRun = true;
	private boolean refLengthsOK = true;

	private CigarParser parser;

	// Stores a hash of @RG (read group) IDs and their associated information
	private HashMap<String, Short> rgHash = new HashMap<>();

	BamFileHandler(IReadCache readCache, ReadSQLCache nameCache, AssemblyFile bamFile, AssemblyFile baiFile, Assembly assembly)
	{
		this.bamFile = bamFile;
		this.baiFile = baiFile;
		this.readCache = readCache;
		this.assembly = assembly;
		this.nameCache = nameCache;
	}

	public void cancel()
		{ okToRun = false; }

	public void loadDataBlock(Contig contig, int s, int e)
			throws Exception
	{
		long start = System.currentTimeMillis();
		Assembly.setIsPaired(false);
		try
		{
			loadData(contig, s, e);
		}
		catch(Exception ex)
		{
			openBamFile(null);
			nameCache = nameCache.resetCache();

			ex.printStackTrace();

			throw new Exception(ex.toString() + "\n\n"
				+ RB.getString("io.BamFileHandler.bamError"), ex);
		}
		System.out.println("Loaded in: " + (System.currentTimeMillis()-start));
	}

	public void loadData(Contig contig, int s, int e)
		throws Exception
	{
		okToRun = true;
		readID = 0;

		// Reset the read cache for each new block of data
		if (readCache instanceof ReadFileCache)
			readCache = ((ReadFileCache)readCache).resetCache();
		else
			readCache = new ReadMemCache();

		nameCache = nameCache.resetCache();

		readCache.openForWriting();
		nameCache.openForWriting();

		parser = new CigarParser(contig, assembly);

		contig.clearContigData(true);

		SAMRecordIterator itor = bamReader.query(contig.getName(), s+1, e+1, false);

		while(itor.hasNext() && okToRun)
		{
			SAMRecord record = itor.next();

			if (!record.getReadUnmappedFlag())
			{
				if (isDummyReadFeature(record) == false)
					createRead(contig, record);
				else
					createDummyFeature(record, contig);
			}
		}

		itor.close();

		if (okToRun)
		{
			parser.processCigarFeatures();
			Collections.sort(contig.getFeatures());
		}

		if (okToRun)
		{
			readCache.openForReading();
			assembly.setReadCache(readCache);
			nameCache.openForReading();
			assembly.setNameCache(nameCache);
		}

		if (okToRun)
		{
			contig.getReads().trimToSize();
			// Sort now happens in DisplayDataCalculator (22/10/2010)
//			Collections.sort(contig.getReads());
			contig.calculateOffsets(assembly);
		}

		if (Assembly.isPaired())
			nameCache.indexNames();
	}

	private void createRead(Contig contig, final SAMRecord record) throws Exception
	{
		int readStartPos = record.getAlignmentStart()-1;

		ReadNameData rnd = new ReadNameData(record.getReadName());
		ReadMetaData rmd = new ReadMetaData(record.getReadNegativeStrandFlag());

		rmd.setIsPaired(record.getReadPairedFlag());

		// Map the read's readgroup to the ID for it (if it exists)
		SAMReadGroupRecord rgRecord = record.getReadGroup();
		if (rgRecord != null)
			rmd.setReadGroup(rgHash.get(rgRecord.getId()));

		Read read;
		// If the read is paired
		if(record.getReadPairedFlag())
		{
			MatedRead pr = new MatedRead(readID, readStartPos);
			pr.setMatePos(record.getMateAlignmentStart()-1);
			read = pr;
			rnd.setInsertSize(Math.abs(record.getInferredInsertSize()));
			rnd.setIsProperPair(record.getProperPairFlag());
			rnd.setMateContig(record.getMateReferenceName());

			rmd.setNumberInPair(record.getFirstOfPairFlag() ? (byte)1 : 2);
			rmd.setMateMapped(!record.getMateUnmappedFlag());

			Assembly.setIsPaired(true);

			boolean isMateContig = record.getMateReferenceName().equals(record.getReferenceName());
			pr.setIsMateContig(isMateContig);
			rmd.setIsMateContig(isMateContig);
		}
		else
			read = new Read(readID, readStartPos);

		String bases = parser.parse(record.getReadString(), readStartPos, record.getCigarString(), read);
		StringBuilder fullRead = new StringBuilder(bases);

		rmd.setData(fullRead);

		int uLength = rmd.calculateUnpaddedLength();
		rnd.setUnpaddedLength(uLength);
		rnd.setCigar(record.getCigar().toString());
		nameCache.setReadNameData(rnd, contig);
		read.setLength(rmd.length());

		contig.getReads().add(read);

		// Do base-position comparison...
		BasePositionComparator.compare(contig, rmd, readStartPos);

		readCache.setReadMetaData(rmd);
		readID++;
	}

	private void createDummyFeature(SAMRecord record, Contig contig)
	{
		if (record.getAttribute("CT") != null)
		{
			String ct = record.getAttribute("CT").toString();
			String[] tagElems = ct.split(";");

			int pos = record.getAlignmentStart()-1;
			int length = parser.calculateLength(record.getCigarString());

			Feature feature = new Feature(tagElems[1], tagElems[1], pos, pos + length);
			feature.setTags(Arrays.copyOfRange(tagElems, 2, tagElems.length));

			contig.addFeature(feature);
			feature.verifyType();
		}
	}

	private boolean isDummyReadFeature(SAMRecord record)
	{
		return record.getReadString().equals("*") && record.getNotPrimaryAlignmentFlag() && record.getReadFailsVendorQualityCheckFlag();
	}

	public void openBamFile(HashMap<String, Contig> contigHash)
		throws Exception
	{
		if (VALIDATION_LENIENT)
			SAMFileReader.setDefaultValidationStringency(SAMFileReader.ValidationStringency.LENIENT);

		if (bamFile.isURL())
			bamReader = new SAMFileReader(bamFile.getURL(), baiFile.getFile(), false);
		else
			bamReader = new SAMFileReader(bamFile.getFile(), baiFile.getFile());

//		if (VALIDATION_LENIENT)
//			bamReader.setValidationStringency(SAMFileReader.ValidationStringency.LENIENT);


		// contigHash will be non-null on first open, so add information on any
		// contigs that weren't in the reference file but are in the BAM file
		if (contigHash != null)
		{
			for(SAMSequenceRecord record : bamReader.getFileHeader().getSequenceDictionary().getSequences())
			{
				String contigName = record.getSequenceName();
				int length = record.getSequenceLength();

				Contig contigToAdd = contigHash.get(contigName);

				if (contigToAdd == null)
				{
					Contig contig = new Contig(contigName);
					contig.getTableData().setConsensusLength(length);

					contigHash.put(contigName, contig);
					assembly.addContig(contig);
				}

				// If it *is* in the imported reference, check the lengths
				else
				{
					int cLength = contigToAdd.getConsensus().length();

					if (length != cLength)
					{
						System.out.println("Contig " + contigToAdd.getName()
							+ " lengths do not match: " + cLength + " (ref "
							+ "file), " + length + " (BAM file)");

						refLengthsOK = false;
					}
				}
			}

			// Run idxstats and try to fill the contigs with read counts
			SamtoolsHelper idxStats = new SamtoolsHelper(contigHash, assembly.getCacheID());
			boolean statsOK = idxStats.run(bamFile, baiFile);

			long totalReadCount = 0;

			// Finally, set every contig to have undefined reads
			for (Contig contig: assembly)
			{
				contig.getTableData().readsDefined = false;
				totalReadCount += contig.getTableData().readCount;

				if (!statsOK)
					contig.getTableData().readCount = -1;
			}

			// Attempt to read sample group information
			readSampleGroups();


			// If all read counts were zero, chances are it's not a "new" index
			if (totalReadCount == 0 && statsOK)
				TaskDialog.warning(
					RB.getString("io.BamFileHandler.noReadsError"),
					RB.getString("gui.text.close"));

			// If idxstats failed (show an error, but we don't quit)
			if (statsOK == false)
			{
				String msg = RB.getString("io.BamFileHandler.statsError1");
				if (Prefs.ioSamtoolsPath.length() > 0)
					msg = RB.getString("io.BamFileHandler.statsError2");

				TaskDialog.showOpenLog(msg, Tablet.getLogFile());
			}
		}
	}

	public SAMFileReader getBamReader()
		{ return bamReader; }

	boolean refLengthsOK()
		{ return refLengthsOK; }

	private void readSampleGroups()
		throws Exception
	{
		ArrayList<ReadGroup> readGroups = new ArrayList<>();

		for (SAMReadGroupRecord record: bamReader.getFileHeader().getReadGroups())
		{
			if (rgHash.get(record.getId()) == null)
			{
				readGroups.add(new ReadGroup(record));
				// Note we put the FIRST read group in as index 1 (not 0)
				rgHash.put(record.getId(), (short)readGroups.size());
			}
		}

		assembly.setReadGroups(readGroups);
	}
}