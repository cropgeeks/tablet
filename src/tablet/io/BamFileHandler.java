package tablet.io;

import java.util.ArrayList;
import java.util.Collections;

import tablet.analysis.*;
import tablet.data.cache.*;
import tablet.data.*;
import tablet.data.auxiliary.*;

import net.sf.samtools.*;
import net.sf.samtools.util.*;

public class BamFileHandler
{
	private IReadCache readCache;
	private SAMFileReader bamReader;
	private Assembly assembly;
	private int readID;

	BamFileHandler(IReadCache readCache, SAMFileReader bamReader, Assembly assembly)
	{
		this.bamReader = bamReader;
		this.readCache = readCache;
		this.assembly = assembly;
	}

	public void loadData(Contig contig, int s, int e)
		throws Exception
	{
		long ts = System.currentTimeMillis();

		// TODO-BAM: need a way to cancel
		readID = 0;

		// Reset the read cache for each new block of data
		if (readCache instanceof ReadFileCache)
			readCache = ((ReadFileCache)readCache).resetCache();
		else
			readCache = new ReadMemCache();

		readCache.openForWriting();

		CigarParser parser = new CigarParser(contig.getName());

		contig.getReads().clear();

		CloseableIterator<SAMRecord> itor = bamReader.query(contig.getName(), s+1, e+1, false);

		while(itor.hasNext())
		{
			SAMRecord record = itor.next();
			if (!record.getReadUnmappedFlag())
			{
				createRead(contig, record, parser);
			}
		}

		itor.close();

		processCigarFeatures(parser, contig);

		readCache.openForReading();
		assembly.setReadCache(readCache);

		contig.getReads().trimToSize();
		Collections.sort(contig.getReads());
		contig.calculateOffsets(assembly);

		long te = System.currentTimeMillis();
		System.out.println("Loaded " + s + "-" + e + " in " + (te-ts) + "ms");
	}

	private void createRead(Contig contig, final SAMRecord record, CigarParser parser) throws Exception
	{
		int readStartPos = record.getAlignmentStart()-1;
		ReadMetaData rmd = new ReadMetaData(record.getReadName(), record.getReadNegativeStrandFlag());

		String fullRead = parser.parse(new String(record.getReadBases()), readStartPos, record.getCigarString());
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

	public SAMFileReader getBamReader()
	{
		return bamReader;
	}
}
