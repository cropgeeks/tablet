// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.util.*;

import tablet.analysis.*;
import tablet.data.cache.*;
import tablet.data.*;
import tablet.data.auxiliary.*;

import net.sf.samtools.*;
import net.sf.samtools.util.*;

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
		try
		{
			loadData(contig, s, e);
		}
		catch(Exception ex)
		{
			openBamFile(null);

			throw new Exception(ex);
		}
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

		CloseableIterator<SAMRecord> itor = bamReader.query(contig.getName(), s+1, e+1, false);

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
			Collections.sort(contig.getReads());
			contig.calculateOffsets(assembly);
		}
	}

	private void createRead(Contig contig, final SAMRecord record, CigarParser parser) throws Exception
	{
		int readStartPos = record.getAlignmentStart()-1;

		ReadNameData rnd = new ReadNameData(record.getReadName());
		
		ReadMetaData rmd = new ReadMetaData(record.getReadNegativeStrandFlag());

		StringBuilder fullRead = new StringBuilder(
			parser.parse(new String(record.getReadBases()), readStartPos, record.getCigarString()));
		rmd.setData(fullRead);

		Read read;
		// If the read is paired
		if(record.getReadPairedFlag() && !record.getMateUnmappedFlag())
		{
			MatedRead pr = new MatedRead(readID, readStartPos);
			pr.setMatePos(record.getMateAlignmentStart()-1);
			read = pr;
			rnd.setInsertSize(record.getInferredInsertSize());
			rnd.setIsProperPair(record.getProperPairFlag());
			rnd.setNumberInPair(record.getFirstOfPairFlag() ? 1 : 2);
			rnd.setMateContig(record.getMateReferenceName());

			// Might want to get rid of this
			if(!Assembly.isPaired())
				Assembly.setIsPaired(true);

			boolean isMateContig = record.getMateReferenceName().equals(record.getReferenceName());
			pr.setIsMateContig(isMateContig);
		}
		else
			read = new Read(readID, readStartPos);
		
		int uLength = rmd.calculateUnpaddedLength();
		rnd.setUnpaddedLength(uLength);
		rnd.setCigar(record.getCigar().toString());
		nameCache.setReadNameData(rnd);
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
			int count = parser.getFeatureMap().get(feature);
			CigarFeature cigarFeature = new CigarFeature("CIGAR-I", "",
				Integer.parseInt(featureElements[1]) - 1, Integer.parseInt(featureElements[1]), count);

			contig.addFeature(cigarFeature);
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

			// Finally, set every contig to have undefined reads
			for (Contig contig: assembly)
				contig.getTableData().readsDefined = false;
		}
	}

	public SAMFileReader getBamReader()
		{ return bamReader; }

	boolean refLengthsOK()
		{ return refLengthsOK; }

}