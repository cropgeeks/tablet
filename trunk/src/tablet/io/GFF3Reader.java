// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
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
	private int cRead, cAdded;

	// Stores a list of features per contig (as they are found)
	private HashMap<String, ArrayList<Feature>> contigs;

	public GFF3Reader(String filename, Assembly assembly)
	{
		AssemblyFile[] files = { new AssemblyFile(filename) };
		setInputs(files, assembly);

		contigs = new HashMap<String, ArrayList<Feature>>();
	}

	public boolean canRead()
		throws Exception
	{
		// Read and check for the header
		in = new BufferedReader(new InputStreamReader(getInputStream(0)));
		str = readLine();

		boolean isGFF3File = false;

		if (str != null && str.trim().toLowerCase().startsWith("##gff-version"))
		{
			try
			{
				// Deals with "##gff-version3" "##gff-version 3" etc
				if (Integer.parseInt(str.substring(13).trim()) == 3)
					isGFF3File = true;
			}
			catch (Exception e) {}
		}

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
			// http://modencode.oicr.on.ca/validate_gff3_online/validate_gff3.html

			// If we find these directives, just quit, as the rest of the file
			// isn't going to be GFF stuff any more
			if (str.startsWith(">") || str.startsWith("##FASTA"))
				break;

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

		// Can we split the tags
		String[] tags = tokens[8].split(";");
		f.setTags(tags);

		getFeatures(contigName).add(f);
		cRead++;
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

			for (Feature feature : newFeatures)
				if (contig.addFeature(feature))
				{
					feature.verifyType();
					cAdded++;
				}

			Collections.sort(contig.getFeatures());
		}
	}

	public int getFeaturesAdded()
		{ return cAdded; }

	public int getFeaturesRead()
		{ return cRead; }
}