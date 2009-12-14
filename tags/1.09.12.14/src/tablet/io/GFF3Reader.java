// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.io;

import java.io.*;
import java.net.*;
import java.util.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import static tablet.io.ReadException.*;

public class GFF3Reader extends TrackableReader
{
	// Stores a list of features per contig (as they are found)
	private HashMap<String, ArrayList<Feature>> contigs;

	public GFF3Reader(File file, Assembly assembly)
	{
		setInputs(new File[] { file }, assembly);

		contigs = new HashMap<String, ArrayList<Feature>>();
	}

	boolean canRead()
		throws Exception
	{
		// Read and check for the header
		in = new BufferedReader(new InputStreamReader(getInputStream(0)));
		str = readLine();

		boolean isGFF3File = (str != null
			&& str.trim().toLowerCase().startsWith("##gff-version3"));

		in.close();
		is.close();

		return isGFF3File;
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		in = new BufferedReader(new InputStreamReader(getInputStream(0)));

		while ((str = readLine()) != null && okToRun)
		{
			// Ignore blank lines or comment lines
			if (str.length() == 0 || str.startsWith("#"))
				continue;

			// Split the line into its 9 tokens
			String[] tokens = str.split("\t");
			if (tokens.length != 9)
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
		String contigName = URLDecoder.decode(tokens[0], "UTF-8");
		int start = Integer.parseInt(tokens[3]);
		int end   = Integer.parseInt(tokens[4]);

		String name = new String(tokens[2].toUpperCase());
		Feature f = new Feature(name, Feature.GFF3, start-1, end-1);

		getFeatures(contigName).add(f);
	}

	// Searches and returns an existing list of features (for a contig). If a
	// list can't be found, then a new one is created (added to the hashtable)
	// and then returned.
	private ArrayList<Feature> getFeatures(String contigName)
	{
		ArrayList<Feature> list = contigs.get(contigName);

		if (list == null)
		{
			list = new ArrayList<Feature>();
			contigs.put(contigName, list);
		}

		return list;
	}

	private void assignFeatures()
	{
		for (Contig contig: assembly)
		{
			ArrayList<Feature> newFeatures = contigs.get(contig.getName());

			if (newFeatures == null)
				continue;

			ArrayList<Feature> features = contig.getFeatures();
			for (Feature newFeature: newFeatures)
				features.add(newFeature);
		}
	}
}