// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.io;

import java.util.*;

import tablet.data.*;
import tablet.data.auxiliary.*;

public abstract class FeatureReader extends TrackableReader
{
	protected int cRead, cAdded;

	// Stores a list of features per contig (as they are found)
	protected HashMap<String, ArrayList<Feature>> contigs;

	public FeatureReader(String filename, Assembly assembly)
	{
		AssemblyFile[] files = { new AssemblyFile(filename) };
		setInputs(files, assembly);

		contigs = new HashMap<String, ArrayList<Feature>>();
	}

	// Searches and returns an existing list of features (for a contig). If a
	// list can't be found, then a new one is created (added to the hashtable)
	// and then returned.
	protected ArrayList<Feature> getFeatures(String contigName)
	{
		ArrayList<Feature> list = contigs.get(contigName);

		if (list == null)
		{
			list = new ArrayList<Feature>();
			contigs.put(contigName, list);
		}

		return list;
	}

	protected void assignFeatures()
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