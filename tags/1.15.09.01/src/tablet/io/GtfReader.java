// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.util.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import static tablet.io.ReadException.*;

public class GtfReader extends FeatureReader
{
	public GtfReader(String filename, Assembly assembly)
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
			// Split the line into its tokens
			String[] tokens = str.split("\t");
			if (tokens.length < 9)
				throw new ReadException(currentFile(), lineCount, TOKEN_COUNT_WRONG);

			processFeature(tokens);
		}

		in.close();

		// If everything loaded in ok, then we can assign what was found
		assignFeatures();
	}

	private void processFeature(String[] tokens)
	{
		// The following columns MUST exist
		String contigName = tokens[0];
		int start = Integer.parseInt(tokens[3]) -1;   // positions are 1-indexed in the .gtf file
		int end = Integer.parseInt(tokens[4]) -1;
		String tName = tokens[2];
		String source = tokens[1];
		String score = tokens[5];
		String strand = tokens[6];
		String frame = tokens[7];

		// The geneId is a mandatory part of the GTF format and is stored in the
		// atrribute list
		String[] attrs = tokens[8].split(";");
		String geneId = attrs[0].trim().substring("gene_id".length()).trim().replaceAll("\"", "");

		// Make the tags string make a little more sense by indtifying the values
		// we've added
		ArrayList<String> tags = new ArrayList<>();
		tags.add("Source: " + source);
		tags.add("Score: " + score);
		tags.add("Strand: " + strand);
		tags.add("Frame: " + frame);
		tags.add(tokens[8]);

		Feature f = new Feature(tName, geneId, start, end);
		f.setTags(tags.toArray(new String[tags.size()]));

		getFeatures(contigName).add(f);
		cRead++;
	}
}