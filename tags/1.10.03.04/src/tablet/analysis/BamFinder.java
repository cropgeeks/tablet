package tablet.analysis;

import java.util.*;
import java.util.regex.*;

import net.sf.samtools.*;
import net.sf.samtools.util.CloseableIterator;

import tablet.data.*;
import tablet.gui.*;
import tablet.gui.viewer.*;

/**
 * Class for searching for reads in BAM files.
 */
public class BamFinder extends Finder
{
	int totalSize = 0;
	String prevContig = "";
	long totalProgress;

	public BamFinder(AssemblyPanel aPanel)
	{
		super(aPanel);
	}

	/**
	 * Search over either a BAM contig, or whole BAM file. Overrides the search
	 * in Finder.
	 */
	@Override
	protected LinkedList<SearchResult> search(String str, int selectedIndex)
	{
		found = 0;
		progress = 0;
		maximum = 0;
		results = new LinkedList<SearchResult>();

		//Temporary SAMFileReader for iterating over whole contig / whole data set
		SAMFileReader reader = aPanel.getAssembly().getBamBam().getBamFileHandler().getBamReader();

		if(searchType == CURRENT_CONTIG)
		{
			totalSize = aPanel.getContig().getDataWidth();
			// Grab iterator for whole contig
			CloseableIterator<SAMRecord> itor = reader.queryOverlapping(aPanel.getContig().getName(), 0, 0);

			// For each read check for matches
			while(itor.hasNext() && okToRun)
			{
				SAMRecord record = itor.next();
		
				checkForMatches(record, str, results, aPanel.getContig());
				// If we've had 500 matches stop searching
				if (results.size() >= 500)
					break;
			}
			// Need to close the iterator as only one is allowed
			itor.close();
		}
		else if(searchType == ALL_CONTIGS)
		{
			// HashMap to allow us to get Contig objects from Contig names
			HashMap<String, Contig> contigs = new HashMap<String, Contig>();

			// Iterate over contigs to get totalSize of all contigs for progress
			// bar. Also to get references to contig objects for the hash.
			for (Contig contig: aPanel.getAssembly())
			{
				totalSize += contig.getDataWidth();
				contigs.put(contig.getName(), contig);
			}

			CloseableIterator<SAMRecord> itor = reader.iterator();
			// For each read check for matches
			while(itor.hasNext() && okToRun)
			{
				SAMRecord record = itor.next();
				
				checkForMatches(record, str, results, contigs.get(record.getReferenceName()));
				//if we've had 500 matches stop searching
				if (results.size() >= 500)
					break;
			}
			// Need to close the iterator as only one is allowed
			itor.close();
		}

		return results;
	}

	/**
	 * Check for matches is the SAM/BAM version of the checkForMatches method in
	 * Finder. This takes a SAMRecord instead of a Read as its first argument.
	 */
	protected void checkForMatches(SAMRecord record, String pattern, LinkedList<SearchResult> results, Contig contig)
	{
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(record.getReadName());

		if ((Prefs.guiRegexSearching && m.matches()) ||
			(!Prefs.guiRegexSearching && record.getReadName().equals(pattern)))
		{
			results.add(new SearchResult(record.getReadName(), record.getUnclippedStart()-1, record.getReadLength(), contig));
			found++;
		}
		if(!prevContig.equals(record.getReferenceName()))
		{
			totalProgress += progress;
			prevContig = record.getReferenceName();
		}
		progress = record.getUnclippedStart();
	}

	@Override
	public int getMaximum()
	{ return 5555; }

	@Override
	public boolean isIndeterminate()
	{ return totalSize == 0; }

	@Override
	public int getValue()
	{
		return Math.round(((totalProgress + progress)/ (float) totalSize) * 5555);
	}
}