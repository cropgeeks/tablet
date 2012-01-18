// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.analysis;

import java.util.*;
import java.util.regex.*;

import tablet.data.*;
import tablet.gui.viewer.*;

public class EnzymeFinder extends Finder
{
	public static final int ENZYME = 3;

	private HashMap<String, String> enzymeHash;

	public EnzymeFinder(AssemblyPanel aPanel, String searchTerm, boolean allContigs)
	{
		super (aPanel, searchTerm, allContigs, ENZYME);
		setupEnzymeHash();
	}

	@Override
	public void runJob(int jobIndex) throws Exception
	{
		createRegex();

		super.runJob(jobIndex);
	}

	@Override
	protected void search()
	{
		for (Contig contig: aPanel.getAssembly())
			if (contig == aPanel.getContig())
				searchForEnzymes(contig.getConsensus().toString(), contig);
	}

	// Does the string matching for restriction enzymes
	protected void searchForEnzymes(String seq, Contig contig)
	{
		String regex = addPadCharClass();
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

		// Look for matches (m.find moves onto the next match where there is one)
		Matcher m = p.matcher(seq);
		while (m.find())
			results.add(new SearchResult(m.start(), m.end()-m.start(), contig));
	}

	private void setupEnzymeHash()
	{
		enzymeHash = new HashMap<String, String>();

		enzymeHash.put("M", "[AC]");
		enzymeHash.put("R", "[AG]");
		enzymeHash.put("W", "[AT]");
		enzymeHash.put("S", "[CG]");
		enzymeHash.put("Y", "[CT]");
		enzymeHash.put("K", "[GT]");
		enzymeHash.put("V", "[ACG]");
		enzymeHash.put("H", "[ACT]");
		enzymeHash.put("D", "[AGT]");
		enzymeHash.put("B", "[CGT]");
		enzymeHash.put("X", "[GATC]");
		enzymeHash.put("N", "[GATC]");
	}

	private void createRegex()
	{
		for (String key : enzymeHash.keySet())
			searchTerm = searchTerm.replaceAll(key, enzymeHash.get(key));
	}
}