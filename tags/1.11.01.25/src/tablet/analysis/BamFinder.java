// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis;

import java.util.*;

import net.sf.samtools.*;
import scri.commons.gui.RB;

import tablet.data.*;
import tablet.gui.Prefs;
import tablet.gui.viewer.*;
import tablet.io.CigarParser;

/**
 * Class for searching for reads in BAM files.
 */
public class BamFinder extends Finder
{
	String prevContig = "";
	long totalProgress;

	public BamFinder(AssemblyPanel aPanel)
	{
		super(aPanel);
	}

	/**
	 * Search over either a BAM contig, or whole BAM file. Overrides the search
	 * in Finder.
	 *
	 * @param searchTerm	The string being compared against.
	 */
	@Override
	protected void search(String searchTerm)
	{
		found = 0;
		progress = 0;
		results = new LinkedList<SearchResult>();

		// Calculate the maximum value for the progress bar.
		calculateMaximum(Prefs.guiFindPanelSelectedIndex);

		//SAMFileReader for iterating over whole contig / whole data set
		SAMFileReader reader = aPanel.getAssembly().getBamBam().getBamFileHandler().getBamReader();

		if(Prefs.guiFindPanelSelectedIndex == CURRENT_CONTIG)
			searchSingleContig(reader, searchTerm);

		else if(Prefs.guiFindPanelSelectedIndex == ALL_CONTIGS)
			searchAllContigs(reader, searchTerm);
	}

	/**
	 * Search over all the contigs in the BAM file.
	 *
	 * @param reader	The SAMFileReader which gives access to the BAM file.
	 * @param searchTerm	The term being compared against.
	 */
	private void searchAllContigs(SAMFileReader reader, String searchTerm)
	{
		// HashMap to allow us to get Contig objects from Contig names
		HashMap<String, Contig> contigs = new HashMap<String, Contig>();

		// Iterate over contigs to get totalSize of all contigs for progress
		// bar. Also to get references to contig objects for the hash.
		for (Contig contig : aPanel.getAssembly())
		{
			totalSize += contig.getDataWidth();
			contigs.put(contig.getName(), contig);
		}

		if(searchType.equals(RB.getString("gui.NBFindPanelControls.findInConsensus")))
		{
			for(Contig contig : aPanel.getAssembly())
			{
				searchReferenceSequence(contig, searchTerm);

				if (results.size() >= Prefs.guiSearchLimit)
					break;
			}
			return;
		}

		SAMRecordIterator itor = reader.iterator();
		// For each read check for matches
		CigarParser parser = new CigarParser();
		while (itor.hasNext() && okToRun && results.size() < Prefs.guiSearchLimit)
		{
			SAMRecord record = itor.next();
			checkRecordForMatches(record, searchTerm, parser, contigs.get(record.getReferenceName()));
		}
		// Need to close the iterator as only one is allowed
		itor.close();
	}

	/**
	 * Search for results in the current contig only.
	 *
	 * @param reader	The SAMFileReader which gives access to the BAM file.
	 * @param searchTerm	The term being compared against.
	 */
	private void searchSingleContig(SAMFileReader reader, String searchTerm)
	{
		totalSize = aPanel.getContig().getDataWidth();
		// Grab iterator for whole contig
		SAMRecordIterator itor = reader.queryOverlapping(aPanel.getContig().getName(), 0, 0);

		// Search the consensus for this one contig
		if(searchType.equals(RB.getString("gui.NBFindPanelControls.findInConsensus")))
		{
			searchReferenceSequence(aPanel.getContig(), searchTerm);
			itor.close();
		}
		// For each read check for matches
		else
		{
			CigarParser parser = new CigarParser();
			while (itor.hasNext() && okToRun && results.size() < Prefs.guiSearchLimit)
			{
				SAMRecord record = itor.next();
				checkRecordForMatches(record, searchTerm, parser, aPanel.getContig());
			}
			// Need to close the iterator as only one is allowed
			itor.close();
		}
	}

	/**
	 * Carry out the steps to check if the provided record contains any matches
	 * to our search term.
	 *
	 * @param record	The record being checked for matches.
	 * @param searchTerm	The term we are checking against.
	 * @param parser	The cigar parser (Required to get full BAM readString).
	 * @param contig	The contig this read / record is contained in.
	 */
	private void checkRecordForMatches(SAMRecord record, String searchTerm, CigarParser parser, Contig contig)
	{
		if (searchReads)
		{
			checkForReadMatches(record.getReadName(), record.getUnclippedStart() - 1, record.getReadLength(), searchTerm, contig);
		}
		else
		{
			try
			{
				String fullRead = parser.parse(record.getReadString(), record.getUnclippedStart() - 1, record.getCigarString(), null);
				checkForSubsequenceMatches(record.getReadName(), record.getUnclippedStart() - 1, record.getReadLength(), searchTerm, contig, fullRead);
			}
			catch (Exception ex)
			{
			}
		}
		updateProgress(record);
	}

	/**
	 * Update the progress bar.
	 *
	 * @param record
	 */
	private void updateProgress(SAMRecord record)
	{
		if (!prevContig.equals(record.getReferenceName()))
		{
			totalProgress += progress;
			prevContig = record.getReferenceName();
		}
		progress = record.getUnclippedStart();
	}

	@Override
	public boolean isIndeterminate()
	{ return totalSize == 0; }

	@Override
	public int getValue()
	{
		return Math.round(((totalProgress + progress)/ (float) totalSize) * 5555);
	}
}