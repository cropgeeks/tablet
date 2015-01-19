// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;

import tablet.data.*;
import tablet.data.auxiliary.*;

import static tablet.io.ReadException.*;

public class VcfReader extends FeatureReader
{
	private String trackName = "VCF";

	public VcfReader(String filename, Assembly assembly)
	{
		super(filename, assembly);
	}

	@Override
	public void runJob(int i)
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(0)));

		while ((str = readLine()) != null && str.length() > 0 && okToRun)
		{
			// Read past the header information
			if (str.startsWith("#"))
				continue;

			// Split the line into its tokens
			String[] tokens = str.split("\t");
			if (tokens.length < 8)
				throw new ReadException(currentFile(), lineCount, TOKEN_COUNT_WRONG);

			processFeature(tokens);
		}

		in.close();

		// If everything loaded in ok, then we can assign what was found
		assignFeatures();
	}

	private void processFeature(String[] tokens)
		throws Exception
	{
		// These three columns MUST exist
		String contigName = tokens[0];
		int start = Integer.parseInt(tokens[1]) -1;   // positions are 1-indexed in the .vcf file

		// Everything else is optional
		String name = tokens[2];

		// Try to extract a track name from the Info field of the VCF entry
		String tName = getTrackName(tokens[7]);
		Feature f = new Feature(tName, name, start, start);

		// Can we include anything else?
		String[] tags = Arrays.copyOfRange(tokens, 3, tokens.length-1);
		f.setTags(tags);

		getFeatures(contigName).add(f);
		cRead++;
	}

	// Search through the Info field looking for the type entry
	private String getTrackName(String info)
	{
		// The info field is semi-colon separated
		String[] infoTokens = info.split(";");
		String tName = "";
		// We're looking for an entry which begins with Type=, the rest of that
		// entry will be our track name
		for (String s : infoTokens)
			if (s.toLowerCase().startsWith("type="))
				tName = s.substring(s.indexOf('=')+1, s.length());

		return tName.isEmpty() ? trackName : tName;
	}
}