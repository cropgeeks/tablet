// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis;

import java.util.*;
import java.util.regex.*;
import javax.swing.*;

import scri.commons.gui.*;

import tablet.data.*;
import tablet.gui.*;
import tablet.gui.viewer.*;

/**
 * Class extends Simplejob such that a search on reads can be run
 * that keeps track of its progress.
 */
public class Finder extends SimpleJob
{
	public static final int CURRENT_CONTIG = 0;
	public static final int ALL_CONTIGS = 1;
	protected int found;
	protected LinkedList<SearchResult> results;
	protected AssemblyPanel aPanel;
	protected String searchTerm;
	protected boolean searchReads;
	protected String searchType;
	int totalSize = 0;

	public Finder(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;
	}

	/**
	 * Searches for the desired read, or subsequence. Returns a LinkedList of
	 * SearchResult objects.
	 *
	 * @param str	The string to be searched for.
	 * @param selectedIndex	The type of search to run.
	 */
	protected void search(String str)
	{
		found = 0;
		progress = 0;
		results = new LinkedList<SearchResult>();

		// Calculate the maximum value for the progress bar.
		calculateMaximum(Prefs.guiFindPanelSelectedIndex);

		boolean searchInConsensus = searchType.equals(RB.getString("gui.NBFindPanelControls.findInConsensus"));
		boolean searchAllContigs = Prefs.guiFindPanelSelectedIndex == ALL_CONTIGS;

		//Loop over contigs checking for matches
		for(Contig contig : aPanel.getAssembly())
		{
			// If we are to search in all contigs, or the current contig is the one to be searched in
			boolean searchContig = (searchAllContigs || (Prefs.guiFindPanelSelectedIndex == CURRENT_CONTIG && contig == aPanel.getContig()));

			// If do search is false we don't need to check anything else; just move on to the next contig.
			if (!searchContig)
				continue;
			
			if (!searchInConsensus && okToRun)
				searchReadsInContig(contig, str);

			else if (searchInConsensus && okToRun)
				searchReferenceSequence(contig, str);

			if (results.size() >= Prefs.guiSearchLimit)
				break;
		}
	}

	/**
	 * Calculate the maximum value for the progress bar, based on the type of
	 * search being carried out.
	 *
	 * @param searchScope
	 */
	protected void calculateMaximum(int searchScope)
	{
		totalSize = 0;
		
		if(searchScope == CURRENT_CONTIG && !searchType.equals(RB.getString("gui.NBFindPanelControls.findInConsensus")))
			totalSize = aPanel.getContig().getReads().size();

		else if(searchScope == CURRENT_CONTIG && searchType.equals(RB.getString("gui.NBFindPanelControls.findInConsensus")))
			totalSize = aPanel.getContig().getConsensus().length();

		else if(searchScope == ALL_CONTIGS && !searchType.equals(RB.getString("gui.NBFindPanelControls.findInConsensus")))
		{
			//Work out the total number of reads across all contigs
			for(Contig contig : aPanel.getAssembly())
				totalSize += contig.getReads().size();
		}

		else if(searchScope == ALL_CONTIGS && searchType.equals(RB.getString("gui.NBFindPanelControls.findInConsensus")))
		{
			for(Contig contig : aPanel.getAssembly())
				totalSize += aPanel.getContig().getConsensus().length();
		}
	}

	/**
	 * Search over all the reads in the given contig using the search string
	 * provided.
	 *
	 * @param contig	The contig to search in.
	 * @param searchString	The string to find.
	 */
	private void searchReadsInContig(Contig contig, String searchString)
	{
		int readNo = 0;
		while((readNo < contig.getReads().size()) && okToRun)
		{
			Read read = contig.getReads().get(readNo);
			ReadMetaData rmd = Assembly.getReadMetaData(read, false);
			ReadNameData rnd = Assembly.getReadNameData(read);

			if (searchReads)
				checkForReadMatches(rnd.getName(), read.getStartPosition(), rmd.length(), searchString, contig);
			else
				checkForSubsequenceMatches(rnd.getName(), read.getStartPosition(), rmd.length(), searchString, contig, rmd.toString());

			progress++;
			//if we've had 500 matches stop searching
			if (results.size() >= Prefs.guiSearchLimit)
				break;

			readNo++;
		}
	}

	/**
	 * Checks to see if the given read name matches the searchString we are
	 * looking for.
	 *
	 * @param readName	The read name to compare against.
	 * @param startPos	The starting position of the read.
	 * @param length	The length of the read.
	 * @param searchString	The string we are comparing the read names against.
	 * @param contig	The contig this read is a part of.
	 */
	protected void checkForReadMatches(String readName, int startPos, int length, String searchString, Contig contig)
	{
		Pattern p = Pattern.compile(searchString);
		Matcher m = p.matcher(readName);

		if ((Prefs.guiRegexSearching && m.matches()) ||
			(!Prefs.guiRegexSearching && readName.equals(searchString)))
		{
			results.add(new ReadSearchResult(readName, startPos, length, contig));
			found++;
		}
	}

	/**
	 * Checks to see if the subsequence we are looking for can be found anywhere
	 * within the read string.
	 *
	 * @param readName	The read name.
	 * @param startPos	The starting position of the read.
	 * @param length	The length of the read.
	 * @param searchString	The string we are comparing the read strings.
	 * @param contig	The contig this read is a part of.
	 * @param readString	The read string to compare against.
	 */
	protected void checkForSubsequenceMatches(String readName, int startPos, int length, String searchString, Contig contig, String readString)
	{
		int index = readString.toLowerCase().indexOf(searchString.toLowerCase());

		// Having found one occurrence in the read string, search for more.
		while(index != -1)
		{
			results.add(new SubsequenceSearchResult(readName, startPos, length, contig, startPos+index, (startPos+index+searchString.length())));
			found++;

			if (results.size() >= Prefs.guiSearchLimit)
				break;

			index = readString.toLowerCase().indexOf(searchString.toLowerCase(), ++index);
		}
	}

	protected void searchReferenceSequence(Contig contig, String str)
	{
		String reference = contig.getConsensus().toString();

		str = str.toUpperCase();

		int index = reference.indexOf(str);

		while(index != -1)
		{
			progress = index;
			results.add(new SearchResult(index, str.length(), contig));
			found++;

			if (results.size() >= Prefs.guiSearchLimit)
				break;

			index = reference.indexOf(str, ++index);
		}
	}


	/**
	 * Run job method so this can be run in a progress dialog.
	 *
	 * @param jobIndex
	 * @throws Exception
	 */
	public void runJob(int jobIndex) throws Exception
	{
		try { Pattern.compile(searchTerm); }
			catch (PatternSyntaxException e)
			{
				TaskDialog.error(
					RB.format("gui.FindPanel.regexError", e.getMessage()),
					RB.getString("gui.text.close"));
				return;
			}

		if(searchTerm.equals(""))
			return;

		search(searchTerm);

		//if we've had 500 matches stop searching
		if (results.size() >= Prefs.guiSearchLimit)
			showWarning();
	}

	public String getMessage()
	{
		return RB.getString("gui.NBFindPanelControls.progressMessage")+ " " + found;
	}

	private void showWarning()
	{
		if (Prefs.guiWarnSearchLimitExceeded)
		{
			String msg = RB.format("gui.findPanel.guiWarnSearchLimitExceeded",
				Prefs.guiSearchLimit);
			JCheckBox checkbox = new JCheckBox();
			RB.setText(checkbox, "gui.findPanel.checkWarning");
			String[] options = new String[]{RB.getString("gui.text.ok")};
			TaskDialog.show(msg, TaskDialog.QST, 0, checkbox, options);
			Prefs.guiWarnSearchLimitExceeded = !checkbox.isSelected();
		}
	}

	public void setResults(LinkedList<SearchResult> results)
	{	this.results = results;	}

	public LinkedList<SearchResult> getResults()
	{	return results;	}

	public void setSearchTerm(String searchTerm)
	{	this.searchTerm = searchTerm;	}

	public void setSearchReads(String searchType)
	{
		if(searchType.equals(RB.getString("gui.NBFindPanelControls.findLabel1")))
			searchReads = true;
		else
			searchReads = false;
	}

	public void setSearchType(String searchType)
	{
		this.searchType = searchType;
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
		return Math.round(((progress)/ (float) totalSize) * 5555);
	}



	public class SearchResult
	{
		private int position;
		private int length;
		private Contig contig;

		SearchResult(int position, int length, Contig contig)
		{
			this.position = position;
			this.length = length;
			this.contig = contig;
		}

		public int getPosition()
		{	return position;	}

		public int getLength()
		{	return length;	}

		public Contig getContig()
		{	return contig;	}
	}



	public class ReadSearchResult extends SearchResult
	{
		private String name;

		ReadSearchResult(String name, int position, int length, Contig contig)
		{
			super(position, length, contig);
			this.name = name;
		}

		public String getName()
		{
			return name;
		}
	}



	public class SubsequenceSearchResult extends ReadSearchResult
	{
		private int sIndex;
		private int eIndex;

		SubsequenceSearchResult(String name, int position, int length, Contig contig, int sIndex, int eIndex)
		{
			super(name, position, length, contig);
			this.sIndex = sIndex;
			this.eIndex = eIndex;
		}

		public int getStartIndex()
		{	return sIndex;	}

		public int getEndIndex()
		{	return eIndex;	}
	}
}