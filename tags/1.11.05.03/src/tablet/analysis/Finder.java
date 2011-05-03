// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis;

import java.util.*;
import java.util.regex.*;
import javax.swing.*;

import scri.commons.gui.*;

import tablet.data.*;
import tablet.data.cache.ReadSQLCache;
import tablet.gui.*;
import tablet.gui.viewer.*;

/**
 * Class extends Simplejob such that a search on reads can be run
 * that keeps track of its progress.
 */
public class Finder extends SimpleJob
{
	public static final int READ_NAME = 0;
	public static final int READ_SEQUENCE = 1;
	public static final int CON_SEQUENCE = 2;

	protected AssemblyPanel aPanel;

	protected String searchTerm;
	protected Pattern pattern;
	protected ArrayList<SearchResult> results;
	protected long maximumLong;
	protected long progressLong;

	protected boolean searchAllContigs;
	protected boolean useRegex;
	protected int searchType;

	public Finder(AssemblyPanel aPanel, String searchTerm, boolean searchAllContigs, int searchType)
	{
		this.aPanel = aPanel;
		this.searchTerm = searchTerm;
		this.searchAllContigs = searchAllContigs;
		this.searchType = searchType;

		useRegex = Prefs.guiRegexSearching;

		results = new ArrayList<SearchResult>();
	}

	// Run job method so this can be run in a progress dialog.
	public void runJob(int jobIndex) throws Exception
	{
		if(searchTerm.equals(""))
			return;

		try
		{
			pattern = Pattern.compile(searchTerm);
		}
		catch (PatternSyntaxException e)
		{
			TaskDialog.error(
				RB.format("gui.FindPanel.regexError", e.getMessage()),
				RB.getString("gui.text.close"));
			return;
		}

		// Calculate the maximum value for the progress bar.
		calculateMaximum();

		search();

		//if we've had 500 matches stop searching
		if (results.size() >= Prefs.guiSearchLimit)
			showWarning();
	}

	protected void search()
	{
		if (searchType == READ_NAME)
			searchReadNames();

		else
		{
			//Loop over contigs checking for matches
			for (Contig contig: aPanel.getAssembly())
			{
				if (!okToRun())
					break;

				// If not searching in all contigs, skip to the current contig
				if (!searchAllContigs && contig != aPanel.getContig())
					continue;

				if (searchType == CON_SEQUENCE)
					searchConsensus(contig);
				else
					searchReadSubsequences(contig);
			}
		}
	}

	protected void searchReadNames()
	{
		int readCount = 0;
		// Count the total number of reads in the dataset
		for (Contig contig : aPanel.getAssembly())
			readCount += contig.getReads().size();

		// The index of the first read returned from the current batch of DB results
		int rIndex = 0;
		// Loop over every read name in the database
		while (rIndex < readCount && okToRun())
		{
			ArrayList<ReadSQLCache.NameWrapper> names = Assembly.getReadNameFinder(rIndex, Prefs.guiSearchDBLimit);

			// The index of the read currently being processed (from the DB)
			int readID = rIndex-1;
			for (ReadSQLCache.NameWrapper wrapper : names)
			{
				if (!okToRun())
					break;

				readID++;
				progressLong = readID;

				// If the name matches the search term, find the read with this name
				if (checkNameMatches(wrapper.name) == false)
					continue;

				Contig contig = aPanel.getAssembly().getContig(wrapper.contigId);
				if (!searchAllContigs && contig != aPanel.getContig())
					continue;

				for (Read read : contig.getReads())
				{
					if (read.getID() == readID)
					{
						results.add(new ReadSearchResult(wrapper.name, read.getStartPosition(), read.length(), contig));
						break;
					}
				}
			}

			rIndex += Prefs.guiSearchDBLimit;
		}
	}

	// Checks to see if the given read name matches the searchString
	protected boolean checkNameMatches(String name)
	{
		Matcher m = pattern.matcher(name);

		if ((useRegex && m.matches()) || (!useRegex && name.equals(searchTerm)))
			return true;

		return false;
	}

	protected void searchConsensus(Contig contig)
	{
		Consensus con = contig.getConsensus();
		searchSequence(con.toString(), 0, con.length(), contig, null);
	}

	protected void searchReadSubsequences(Contig contig)
	{
		for (Read read : contig.getReads())
		{
			if (!okToRun())
				break;

			ReadMetaData rmd = Assembly.getReadMetaData(read, true);

			String name = Assembly.getReadName(read);
			searchSequence(rmd.toString(), read.getStartPosition(), read.length(), contig, name);
		}
	}

	// Does the string matching for subsequence and consensus / reference
	protected void searchSequence(String seq, int sPos, int len, Contig contig, String name)
	{
		int matchIndex, matchS;
		matchIndex = matchS = 0;

		for (int i = 0; i < seq.length() && okToRun(); i++)
		{
			char seqChar = seq.charAt(i);

			if (seqChar == searchTerm.charAt(matchIndex))
			{
				// Denotes the start of a potential match
				if (matchIndex == 0)
					matchS = i;

				matchIndex++;
			}

			// Skip pad characters
			else if (Prefs.guiSearchIgnorePads && (seqChar == '*' || seqChar == 'N'))
				continue;

			else
			{
				// Jump back to next potential match start character
				if (matchIndex > 0 && matchS != seq.length()-1)
					i = matchS;
				matchIndex = 0;
			}

			// We have found a result
			if (matchIndex == searchTerm.length())
			{
				if (searchType == CON_SEQUENCE)
					results.add(new SearchResult(matchS, i - matchS + 1, contig));
				else
					results.add(new SubsequenceSearchResult(name, sPos, len, contig, sPos + matchS, sPos + i));

				matchIndex = 0;

				if (matchS != seq.length()-1)
					i = matchS;
			}
		}
	}

	// Calculate the maximum for the progress bar based on search type
	protected void calculateMaximum()
	{
		maximumLong = 0;

		// Read search in current contig
		if(!searchAllContigs && searchType == READ_SEQUENCE)
			maximumLong = aPanel.getContig().getReads().size();

		// Read search across all contigs
		else if(searchType == READ_NAME || (searchAllContigs && searchType == READ_SEQUENCE))
			for(Contig contig : aPanel.getAssembly())
				maximumLong += contig.getReads().size();

		// Consensus search in current contig
		else if(!searchAllContigs && searchType == CON_SEQUENCE)
			maximumLong = aPanel.getContig().getConsensus().length();

		// Consensus search across all contigs
		else if(searchAllContigs  && searchType == CON_SEQUENCE)
			for(Contig contig : aPanel.getAssembly())
				maximumLong += contig.getConsensus().length();
	}

	public String getMessage()
	{
		return RB.getString("gui.NBFindPanelControls.progressMessage") + " " +
			results.size();
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

	public void setResults(ArrayList<SearchResult> results)
		{ this.results = results; }

	public ArrayList<SearchResult> getResults()
		{ return results; }

	public boolean okToRun()
	{
		return okToRun && results.size() < Prefs.guiSearchLimit;
	}

	public int getMaximum()
		{ return 5555; }

	public boolean isIndeterminate()
		{ return maximumLong == 0; }

	// Doing this with the 5555 trick because the maximum may be set to the total
	// number of reads which may be beyond the limit of an int
	public int getValue()
	{
		return Math.round(((progressLong)/ (float) maximumLong) * 5555);
	}


	// Used for storing consensus subsequence search results
	public static class SearchResult
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
			{ return position; }

		public int getLength()
			{ return length; }

		public Contig getContig()
			{ return contig; }
	}


	// Used for storing read name search results
	public static class ReadSearchResult extends SearchResult
	{
		private String name;

		ReadSearchResult(String name, int position, int length, Contig contig)
		{
			super(position, length, contig);
			this.name = name;
		}

		public String getName()
			{ return name; }
	}


	// Used for sotring read subsequence search results
	public static class SubsequenceSearchResult extends ReadSearchResult
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
			{ return sIndex; }

		public int getEndIndex()
			{ return eIndex; }
	}
}