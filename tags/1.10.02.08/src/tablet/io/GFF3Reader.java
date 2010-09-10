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

	public GFF3Reader(String filename, Assembly assembly)
	{
		AssemblyFile[] files = { new AssemblyFile(filename) };
		setInputs(files, assembly);

		contigs = new HashMap<String, ArrayList<Feature>>();
	}

	boolean canRead()
		throws Exception
	{
		// Read and check for the header
		in = new BufferedReader(new InputStreamReader(getInputStream(0, true)));
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
		in = new BufferedReader(new InputStreamReader(getInputStream(0, true)));

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

		// See if we can parse out a name
		// A name must be formed from "Name=[name];" (not name=) and specical
		// characters must be URL encoded. The ending ; won't be there if it's
		// the last tag in the string
		String name = "";
		int index1 = tokens[8].indexOf("Name=");
		if (index1 != -1)
		{
			int index2 = tokens[8].indexOf(";", index1);
			if (index2 != -1)
				name = tokens[8].substring(index1+5, index2);
			else
				name = tokens[8].substring(index1+5);

			name = URLDecoder.decode(name, "UTF-8");
		}

		String gffType = new String(tokens[2].toUpperCase());
		Feature f = new Feature(Feature.GFF3, gffType, name, start-1, end-1);

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

			loop:
			for (Feature newFeature: newFeatures)
			{
				// Check it doesn't already exist
				for (int i = 0; i < features.size(); i++)
					if (features.get(i).isSameAs(newFeature))
						continue loop;

				features.add(newFeature);
			}

			Collections.sort(features);
		}
	}
}