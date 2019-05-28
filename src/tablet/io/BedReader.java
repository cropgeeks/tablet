// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.net.*;
import java.util.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import static tablet.io.ReadException.*;

public class BedReader extends FeatureReader
{
	private String trackName = "BED";

	public BedReader(String filename, Assembly assembly)
	{
		super(filename, assembly);
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(0)));

		while ((str = readLine()) != null && str.length() > 0 && okToRun)
		{
			// http://www.ensembl.org/info/website/upload/bed.html

			// Is this the start of a new track?
			//   track name="ItemRGBDemo"
			if (str.startsWith("track "))
			{
				if (readTrack(str) == false)
					if (readTrackTopHat(str) == false)
						trackName = "BED";

				continue;
			}

			// Split the line into its tokens
			String[] tokens = str.split("\t");
			if (tokens.length < 3)
				throw new ReadException(currentFile(), lineCount, TOKEN_COUNT_WRONG);

			processFeature(tokens);
		}

		in.close();

		// If everything loaded in ok, then we can assign what was found
		assignFeatures();
	}

	private boolean readTrack(String str)
	{
		try
		{
			// Strip out the name: from the fixed (12) position of the 1st
			// quotes to a search for '" ' (closing quotes plus space)
			trackName = str.substring(12, str.indexOf("\" ", 12));
			return true;
		}
		catch (Exception e) { System.out.println("###");return false; }
	}

	// TopHat (as of April 2014) seems to produce track lines with no quotes
	// around the name...
	private boolean readTrackTopHat(String str)
	{
		try
		{
			trackName = str.substring(11, str.indexOf(" ", 11));
			return true;
		}
		catch (Exception e) { System.out.println("#####");return false; }
	}

	private void processFeature(String[] tokens)
		throws Exception
	{
		// These three columns MUST exist
		String contigName = tokens[0];
		int start = Integer.parseInt(tokens[1]);   // positions are ALREADY 0-indexed in the .bed file!!!
		int end   = Integer.parseInt(tokens[2]);

		// Everything else is optional
		String name = "";
		if (tokens.length > 3)
			name = tokens[3];


		Feature f = new Feature(trackName, name, start, end);

		// Can we include anything else?
		if (tokens.length > 4)
		{
			String[] tags = Arrays.copyOfRange(tokens, 4, tokens.length-1);
			f.setTags(tags);
		}

		getFeatures(contigName).add(f);
		cRead++;
	}
}