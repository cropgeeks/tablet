// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data.auxiliary;

import java.util.*;

import tablet.data.*;

/**
 * Simple class to encapsulate a list of features (on one "track").
 */
public class FeatureTrack
{
	private ArrayList<Feature> features = new ArrayList<Feature>();

	public FeatureTrack()
	{
	}

	/**
	 * Adds a new feature to this track without performing checks to ensure it
	 * is unique or inserted at the correct (sorted) position). Use when
	 * building visual tracks from an existing - and sorted - data track
	 */
	public void addFeatureNoSort(Feature feature)
	{
		features.add(feature);
	}

	/**
	 * Adds a new feature to this track, checking to see whether it exists first
	 * or not, and if it doesn't, also ensuring it is added at the correct
	 * location. Use when importing features to a data set.
	 */
	public void addFeatureDoSort(Feature feature)
	{
		int result = Collections.binarySearch(features, feature);

		// If result >= 0 we've found a duplicate and don't add. Otherwise add.
		if (result < 0)
			features.add((-result)-1, feature);
	}

	public int size()
	{
		return features.size();
	}

	public ArrayList<Feature> getFeatures()
	{
		return features;
	}
}