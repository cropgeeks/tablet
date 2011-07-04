// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis;

import java.util.*;
import java.util.regex.*;
import javax.swing.*;

import tablet.data.*;
import tablet.data.cache.*;
import tablet.gui.*;
import tablet.gui.viewer.*;

import scri.commons.gui.*;

/**
 * Class extends Simplejob such that a search on reads can be run
 * that keeps track of its progress.
 */
public class Finder extends SimpleJob
{
	public static final int READ_NAME = 0;
	public static final int READ_SEQUENCE = 1;
	public static final int CON_SEQUENCE = 2;
	public static final int ENZYME = 3;

	public static String AMBIG_M = "[AC]";
	public static String AMBIG_R = "[AG]";
	public static String AMBIG_W = "[AT]";
	public static String AMBIG_S = "[CG]";
	public static String AMBIG_Y = "[CT]";
	public static String AMBIG_K = "[GT]";
	public static String AMBIG_V = "[ACG]";
	public static String AMBIG_H = "[ACT]";
	public static String AMBIG_D = "[AGT]";
	public static String AMBIG_B = "[CGT]";
	public static String AMBIG_XN = "[GATC]";

	private HashMap<String, String> enzymeHash;

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

		setupEnzymeHash();
	}

	// Run job method so this can be run in a progress dialog.
	public void runJob(int jobIndex) throws Exception
	{
		if (searchTerm.isEmpty())
			return;

		// TODO implement method fully
		if (searchType == ENZYME)
			createRegex();

		try
		{
			pattern = Pattern.compile(searchTerm, Pattern.CASE_INSENSITIVE);
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
				else if (searchType == ENZYME)
					searchForEnzymes(contig.getConsensus().toString(), 0,
					contig.getConsensus().length(), contig, null);
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
		// Get sequence needed to avoid crapout
		con.getSequence();
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
		Pattern p;

		if (Prefs.guiSearchIgnorePads)
		{
			String charClass = "[" + Pattern.quote("*") + "N]*";
			String regex = addPadCharClass(charClass);

			p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		}

		else
			p = pattern;

		Matcher m = p.matcher(seq);

		while (m.find())
		{
			if (searchType == CON_SEQUENCE)
				results.add(new SearchResult(m.start(), m.end()-m.start(), contig));
			else
				results.add(new SubsequenceSearchResult(name, sPos, len, contig, sPos+m.start(), sPos+m.end()-1));
		}
	}

	// Does the string matching for restriction enzyme
	protected void searchForEnzymes(String seq, int sPos, int len, Contig contig, String name)
	{
		// Create a pattern which will ignore pad characters
		String charClass = "[" + Pattern.quote("*")+ "]*";
		String regex = addPadCharClass(charClass);

		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

		// Search the sequence looking for matches
		Matcher m = p.matcher(seq);
		while (m.find())
			results.add(new SearchResult(m.start(), m.end()-m.start(), contig));
	}

	private String addPadCharClass(String charClass)
	{
		StringBuilder regex = new StringBuilder();

		for (int i=0; i < searchTerm.length(); i++)
		{
			regex.append(searchTerm.charAt(i));
			if (i < searchTerm.length()-1)
				regex.append(charClass);
		}

		return regex.toString();
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

	private void setupEnzymeHash()
	{
		enzymeHash = new HashMap<String, String>();

		enzymeHash.put("M", AMBIG_M);
		enzymeHash.put("R", AMBIG_R);
		enzymeHash.put("W", AMBIG_W);
		enzymeHash.put("S", AMBIG_S);
		enzymeHash.put("Y", AMBIG_Y);
		enzymeHash.put("K", AMBIG_K);
		enzymeHash.put("V", AMBIG_V);
		enzymeHash.put("H", AMBIG_H);
		enzymeHash.put("D", AMBIG_D);
		enzymeHash.put("B", AMBIG_B);
		enzymeHash.put("X", AMBIG_XN);
		enzymeHash.put("N", AMBIG_XN);
	}

	private void createRegex()
	{
		for (String key : enzymeHash.keySet())
			searchTerm = searchTerm.replaceAll(key, enzymeHash.get(key));
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