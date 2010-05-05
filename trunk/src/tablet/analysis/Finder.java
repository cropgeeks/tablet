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

		if(str.equals(""))
			return;

		//Loop over contigs checking for matches
		for(Contig contig : aPanel.getAssembly())
		{
			if(okToRun)
			{
				if((Prefs.guiFindPanelSelectedIndex == ALL_CONTIGS || (Prefs.guiFindPanelSelectedIndex == CURRENT_CONTIG && contig == aPanel.getContig())) && okToRun)
					searchReadsInContig(contig, str);

				if (results.size() >= 500)
					break;
			}
		}
	}

	/**
	 * Calculate the maximum value for the progress bar, based on the type of
	 * search being carried out.
	 *
	 * @param searchType
	 */
	private void calculateMaximum(int searchType)
	{
		if(searchType == CURRENT_CONTIG)
			totalSize = aPanel.getContig().getReads().size();

		else if(searchType == ALL_CONTIGS)
		{
			//Work out the total number of reads across all contigs
			for(Contig contig : aPanel.getAssembly())
				totalSize += contig.getReads().size();
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

			if (searchReads)
				checkForReadMatches(rmd.getName(), read.getStartPosition(), rmd.length(), searchString, contig);
			else
				checkForSubsequenceMatches(rmd.getName(), read.getStartPosition(), rmd.length(), searchString, contig, rmd.toString());

			progress++;
			//if we've had 500 matches stop searching
			if (results.size() >= 500)
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
			results.add(new SearchResult(readName, startPos, length, contig));
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
		Pattern p = Pattern.compile(searchString);
		Matcher m = p.matcher(readString);

		int index = readString.indexOf(searchString);

		// Having found one occurrence in the read string, search for more.
		while(index != -1)
		{
			results.add(new SubsequenceSearchResult(readName, startPos, length, contig, startPos+index, (startPos+index+searchString.length())));
			found++;
			
			index = readString.indexOf(searchString, ++index);
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
		if (results.size() >= 500)
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
			String msg = RB.getString("gui.findPanel.guiWarnSearchLimitExceeded");
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
		private String name;
		private int position;
		private int length;
		private Contig contig;

		SearchResult(String name, int position, int length, Contig contig)
		{
			this.name = name;
			this.position = position;
			this.length = length;
			this.contig = contig;
		}

		public String getName()
		{	return name;	}

		public int getPosition()
		{	return position;	}

		public int getLength()
		{	return length;	}

		public Contig getContig()
		{	return contig;	}
	}


	
	public class SubsequenceSearchResult extends SearchResult
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