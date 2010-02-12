package tablet.io;

import java.util.Collections;

import tablet.analysis.*;
import tablet.data.cache.*;
import tablet.data.*;
import tablet.data.auxiliary.*;

import net.sf.samtools.*;
import net.sf.samtools.util.*;

public class BaiFileHandler
{
	private IReadCache readCache;
	private SAMFileReader bamReader;
	private Assembly assembly;
	private int readID;

	BaiFileHandler(IReadCache readCache, SAMFileReader bamReader, Assembly assembly)
	{
		this.bamReader = bamReader;
		this.readCache = readCache;
		this.assembly = assembly;
	}

	public void loadData(Contig contig, int s, int e)
		throws Exception
	{
		// TODO-BAM need a way to cancel
		long ts = System.currentTimeMillis();

		readID = 0;

		readCache = new ReadMemCache();
		readCache.openForWriting();

		CigarParser parser = new CigarParser(contig.getName());

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
		// TODO-BAM is this needed? (overhanging reads shouldn't happen) [do mismatch?]
		contig.calculateOffsets();

		long te = System.currentTimeMillis();
		System.out.println("Loaded " + s + "-" + e + " in " + (te-ts) + "ms");
	}

	private void createRead(Contig contigToAddTo, final SAMRecord record, CigarParser parser) throws Exception
	{
		int readStartPos = record.getAlignmentStart()-1;
		ReadMetaData rmd = new ReadMetaData(record.getReadName(), record.getReadNegativeStrandFlag());

		String fullRead = parser.parse(new String(record.getReadBases()), readStartPos, record.getCigarString());
		rmd.setData(fullRead);
		Read read = new Read(readID, readStartPos);

		rmd.calculateUnpaddedLength();
		read.setLength(rmd.length());
		contigToAddTo.getReads().add(read);

		// Do base-position comparison...

		BasePositionComparator.compare(contigToAddTo, rmd, readStartPos);

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
			
			contig.getFeatures().add(cigarFeature);
		}
		Collections.sort(contig.getFeatures());
	}
}
