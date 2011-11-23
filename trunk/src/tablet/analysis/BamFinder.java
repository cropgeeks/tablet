// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis;

import net.sf.samtools.*;

import tablet.data.*;
import tablet.gui.viewer.*;
import tablet.io.*;

/**
 * Class for searching for reads in BAM files.
 */
public class BamFinder extends Finder
{
	private String prevContig = "";
	private long totalProgress;
	private SAMFileReader reader;
	private CigarParser parser;

	public BamFinder(AssemblyPanel aPanel, String searchTerm, boolean searchAllContigs, int searchType) throws Exception
	{
		super(aPanel, searchTerm, searchAllContigs, searchType);

		reader = aPanel.getAssembly().getBamBam().getBamFileHandler().getBamReader();
		parser = new CigarParser();
	}

	@Override
	protected void calculateMaximum()
	{
		if (!allContigs)
			maximumLong = aPanel.getContig().getTableData().readCount;
//			maximumLong += reader.getFileHeader().getSequenceDictionary().getSequence(
//				aPanel.getContig().getName()).getSequenceLength();

		else
			for (Contig contig : aPanel.getAssembly())
				maximumLong += contig.getTableData().readCount;
//			for(SAMSequenceRecord record : reader.getFileHeader().getSequenceDictionary().getSequences())
//				maximumLong += record.getSequenceLength();
	}

	@Override
	protected void searchReadNames()
	{
		for (Contig contig : aPanel.getAssembly())
		{
			if (!okToRun())
				break;

			// If not searching in all contigs, skip to the current contig
			if (!allContigs && contig != aPanel.getContig())
				continue;

			SAMRecordIterator itor = reader.queryOverlapping(contig.getName(), 0, 0);

			while (itor.hasNext() && okToRun())
			{
				SAMRecord record = itor.next();
				if (checkNameMatches(record.getReadName()) && record.getReadUnmappedFlag() == false)
					results.add(new ReadSearchResult(record.getReadName(), record.getAlignmentStart() - 1, record.getReadLength(), contig));

				progressLong++;
			}
			itor.close();
		}
	}

	@Override
	protected void searchReadSubsequences(Contig contig)
	{
		SAMRecordIterator itor = reader.queryOverlapping(contig.getName(), 0, 0);

		while (itor.hasNext() && okToRun())
		{
			SAMRecord record = itor.next();

			try
			{
				String read = parser.parse(record.getReadString(), record.getAlignmentStart() - 1, record.getCigarString(), null);
				searchSequence(read, record.getAlignmentStart() - 1, record.getReadLength(), contig, record.getReadName());
			}
			catch (Exception e) {}

			progressLong++;
		}
		itor.close();
	}
}