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
	protected LinkedList<SearchResult> results;
	protected int found;
	protected AssemblyPanel aPanel;
	protected String searchTerm;
	
	public static final int CURRENT_CONTIG = 0;
	public static final int ALL_CONTIGS = 1;

	protected int searchType = CURRENT_CONTIG;

	public Finder(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;
	}

	protected LinkedList<SearchResult> search(String str, int selectedIndex)
	{
		found = 0;
		progress = 0;
		maximum = 0;
		results = new LinkedList<SearchResult>();

		//Work out the total number of reads across all contigs
		for(Contig contig : aPanel.getAssembly())
		{
			maximum += contig.getReads().size();
		}

		//Loop over contigs checking for matches
		for(Contig contig : aPanel.getAssembly())
		{
			if(selectedIndex == ALL_CONTIGS || (selectedIndex == CURRENT_CONTIG && contig == aPanel.getContig()))
			{
				if(selectedIndex == 0)
				{
					maximum = contig.getReads().size();
				}
				for(Read read : contig.getReads())
				{
					if(okToRun)
					{
						checkForMatches(read, str, results, contig);
						//if we've had 500 matches stop searching
						if (results.size() >= 500)
							break;
					}
				}
			}

			if (results.size() >= 500)
			{
				break;
			}
		}

		return results;
	}

	/**
	 * Method which collates the results which match the search query
	 *
	 * @param read
	 * @param str
	 * @param results
	 * @param contig
	 */
	protected void checkForMatches(Read read, String pattern, LinkedList<SearchResult> results, Contig contig)
	{
		ReadMetaData rmd = Assembly.getReadMetaData(read, false);

		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(rmd.getName());

		if ((Prefs.guiRegexSearching && m.matches()) ||
			(!Prefs.guiRegexSearching && rmd.getName().equals(pattern)))
		{
			results.add(new SearchResult(rmd.getName(), read.getStartPosition(), rmd.length(), contig));
			found++;
		}
		progress++;
	}

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

		results = search(searchTerm, searchType);

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
	{
		this.results = results;
	}

	public LinkedList<SearchResult> getResults()
	{
		return results;
	}

	public void setSearchTerm(String searchTerm)
	{
		this.searchTerm = searchTerm;
	}

	public void setSearchType(int searchType)
	{
		this.searchType = searchType;
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
		{
			return name;
		}

		public int getPosition()
		{
			return position;
		}

		public int getLength()
		{
			return length;
		}

		public Contig getContig()
		{
			return contig;
		}
	}
}