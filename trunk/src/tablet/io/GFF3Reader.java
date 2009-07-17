package tablet.io;

import java.io.*;
import java.net.*;
import java.util.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.data.cache.*;
import tablet.gui.*;
import static tablet.io.ReadException.*;

import scri.commons.file.*;

public class GFF3Reader extends TrackableReader
{
	// Stores a list of features per contig (as they are found)
	private Hashtable<String, Vector<Feature>> contigs;

	public GFF3Reader(File file, Assembly assembly)
	{
		setInputs(file, assembly);

		contigs = new Hashtable<String, Vector<Feature>>();
	}

	boolean canRead()
		throws Exception
	{
		// Read and check for the header
		in = new BufferedReader(new InputStreamReader(getInputStream()));
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
		in = new BufferedReader(new InputStreamReader(getInputStream()));

		while ((str = readLine()) != null && okToRun)
		{
			// Ignore blank lines or comment lines
			if (str.length() == 0 || str.startsWith("#"))
				continue;

			// Split the line into its 9 tokens
			String[] tokens = str.split("\t");
			if (tokens.length != 9)
				throw new ReadException(TOKEN_COUNT_WRONG, lineCount);

			if (tokens[2].toUpperCase().equals("SNP"))
				processSNP(tokens);
		}

		in.close();

		// If everything loaded in ok, then we can assign what was found
		assignFeatures();
	}

	private void processSNP(String[] tokens)
		throws Exception
	{
		String contigName = URLDecoder.decode(tokens[0], "UTF-8");
		int start = Integer.parseInt(tokens[3]);
		int end   = Integer.parseInt(tokens[4]);

		Feature snp = new Feature(Feature.SNP, start-1, end-1);

		getFeatures(contigName).add(snp);
	}

	// Searches and returns an existing list of features (for a contig). If a
	// list can't be found, then a new one is created (added to the hashtable)
	// and then returned.
	private Vector<Feature> getFeatures(String contigName)
	{
		Vector<Feature> list = contigs.get(contigName);

		if (list == null)
		{
			list = new Vector<Feature>();
			contigs.put(contigName, list);
		}

		return list;
	}

	private void assignFeatures()
	{
		for (Contig contig: assembly)
		{
			Vector<Feature> newFeatures = contigs.get(contig.getName());

			if (newFeatures == null)
				continue;

			Vector<Feature> features = contig.getFeatures();
			for (Feature newFeature: newFeatures)
				features.add(newFeature);
		}
	}
}