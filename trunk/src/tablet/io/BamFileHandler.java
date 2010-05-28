// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.util.Collections;

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
	private AssemblyFile bamFile, baiFile;
	private SAMFileReader bamReader;
	private Assembly assembly;
	private int readID;

	private boolean okToRun = true;

	BamFileHandler(IReadCache readCache, AssemblyFile bamFile, AssemblyFile baiFile, Assembly assembly)
	{
		this.bamFile = bamFile;
		this.baiFile = baiFile;
		this.readCache = readCache;
		this.assembly = assembly;
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
			openBamFile();
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

		readCache.openForWriting();

		CigarParser parser = new CigarParser(contig.getName());

		contig.clearContigData(true);

		CloseableIterator<SAMRecord> itor = bamReader.query(contig.getName(), s+1, e+1, false);

		while(itor.hasNext() && okToRun)
		{
			SAMRecord record = itor.next();
			if (!record.getReadUnmappedFlag())
			{
				createRead(contig, record, parser);
			}
		}

		itor.close();

		if (okToRun)
			processCigarFeatures(parser, contig);

		if (okToRun)
		{
			readCache.openForReading();
			assembly.setReadCache(readCache);
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
		ReadMetaData rmd = new ReadMetaData(record.getReadName(), record.getReadNegativeStrandFlag());

		StringBuilder fullRead = new StringBuilder(
			parser.parse(new String(record.getReadBases()), readStartPos, record.getCigarString()));
		rmd.setData(fullRead);
		Read read = new Read(readID, readStartPos);

		rmd.calculateUnpaddedLength();
		read.setLength(rmd.length());
		contig.getReads().add(read);

		// Do base-position comparison...
		BasePositionComparator.compare(contig, rmd, readStartPos);

		rmd.setCigar(record.getCigar().toString());

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

	public void openBamFile() throws Exception
	{
		if (bamFile.isURL())
			bamReader = new SAMFileReader(bamFile.getURL(), baiFile.getFile(), false);
		else
			bamReader = new SAMFileReader(bamFile.getFile(), baiFile.getFile());

		if (VALIDATION_LENIENT)
			bamReader.setValidationStringency(SAMFileReader.ValidationStringency.LENIENT);
	}

	public SAMFileReader getBamReader()
		{	return bamReader;	}
}