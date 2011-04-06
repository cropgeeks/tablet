// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.util.*;

import tablet.analysis.*;
import tablet.data.cache.*;
import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.*;
import tablet.io.samtools.*;

import net.sf.samtools.*;
import net.sf.samtools.util.*;

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

		CigarParser parser = new CigarParser(contig.getName());

		contig.clearContigData(true);

		SAMRecordIterator itor = bamReader.query(contig.getName(), s+1, e+1, false);

		while(itor.hasNext() && okToRun)
		{
			SAMRecord record = itor.next();
			if (!record.getReadUnmappedFlag())
				createRead(contig, record, parser);
		}

		itor.close();

		if (okToRun)
			processCigarFeatures(parser, contig);

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

	private void createRead(Contig contig, final SAMRecord record, CigarParser parser) throws Exception
	{
		int readStartPos = record.getAlignmentStart()-1;

		ReadNameData rnd = new ReadNameData(record.getReadName());

		ReadMetaData rmd = new ReadMetaData(record.getReadNegativeStrandFlag());

		rmd.setIsPaired(record.getReadPairedFlag());

		Read read;
		// If the read is paired
		if(record.getReadPairedFlag())
		{
			MatedRead pr = new MatedRead(readID, readStartPos);
			pr.setMatePos(record.getMateAlignmentStart()-1);
			read = pr;
			rnd.setInsertSize(Math.abs(record.getInferredInsertSize()));
			rnd.setIsProperPair(record.getProperPairFlag());
			rnd.setNumberInPair(record.getFirstOfPairFlag() ? 1 : 2);
			rnd.setMateContig(record.getMateReferenceName());

			rmd.setNumberInPair(record.getFirstOfPairFlag() ? 1 : 2);
			rmd.setMateMapped(!record.getMateUnmappedFlag());

			Assembly.setIsPaired(true);

			boolean isMateContig = record.getMateReferenceName().equals(record.getReferenceName());
			pr.setIsMateContig(isMateContig);
		}
		else
			read = new Read(readID, readStartPos);

		StringBuilder fullRead = new StringBuilder(
			parser.parse(new String(record.getReadBases()), readStartPos, record.getCigarString(), read));
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

	private void processCigarFeatures(CigarParser parser, Contig contig)
		throws Exception
	{
		for (String feature : parser.getFeatureMap().keySet())
		{
			String[] featureElements = feature.split("Tablet-Separator");
			CigarFeature cigarFeature = parser.getFeatureMap().get(feature);

			if (contig.addFeature(cigarFeature))
				cigarFeature.verifyType();
		}

		Collections.sort(contig.getFeatures());
	}

	public void openBamFile(HashMap<String, Contig> contigHash)
		throws Exception
	{
		if (bamFile.isURL())
			bamReader = new SAMFileReader(bamFile.getURL(), baiFile.getFile(), false);
		else
			bamReader = new SAMFileReader(bamFile.getFile(), baiFile.getFile());

		if (VALIDATION_LENIENT)
			bamReader.setValidationStringency(SAMFileReader.ValidationStringency.LENIENT);


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
			IdxStats idxStats = new IdxStats(contigHash, assembly.getCacheID());
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

				TaskDialog.showFileOpen(msg, TaskDialog.ERR, 0,
					new String[] { RB.getString("gui.text.openLog"), RB.getString("gui.text.close") },
					new boolean [] { true, true },
					Tablet.getLogFile().getAbsolutePath());
			}
		}
	}

	public SAMFileReader getBamReader()
		{ return bamReader; }

	boolean refLengthsOK()
		{ return refLengthsOK; }
}