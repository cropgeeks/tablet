// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.analysis;

import tablet.data.*;
import tablet.gui.viewer.*;

import htsjdk.samtools.*;

/**
 * Class for searching for reads in BAM files.
 */
public class BamFinder extends Finder
{
	private SamReader reader;

	public BamFinder(AssemblyPanel aPanel, String searchTerm, boolean searchAllContigs, int searchType) throws Exception
	{
		super(aPanel, searchTerm, searchAllContigs, searchType);

		reader = aPanel.getAssembly().getBamBam().getBamFileHandler().getBamReader();
	}

	@Override
	protected void calculateMaximum()
	{
		if (!allContigs)
			maximumLong = aPanel.getContig().getTableData().readCount;

		else
			for (Contig contig : aPanel.getAssembly())
				maximumLong += contig.getTableData().readCount;
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
					results.add(new ReadSearchResult(record.getReadName(), record.getAlignmentStart() - 1, record.getReadLength(), contig, false));

				progressLong++;
			}
			itor.close();
		}
	}
}