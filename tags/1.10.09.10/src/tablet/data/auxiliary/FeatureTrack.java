// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data.auxiliary;

import java.util.*;

import tablet.data.*;

/**
 * Simple class to encapsulate a list of features (on one "track").
 */
public class FeatureTrack implements Comparator<Feature>
{
	private String name;

	// A "track" might be a container for sub tracks...
	private ArrayList<FeatureTrack> tracks = new ArrayList<FeatureTrack>();

	// ... or just a standard track with some features on it
	private ArrayList<Feature> features = new ArrayList<Feature>();


	public FeatureTrack()
	{
	}

	public FeatureTrack(String name)
		{ this.name = name; }

	public int getSubTrackCount()
		{ return tracks.size(); }

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
	public boolean addFeatureDoSort(Feature feature)
	{
		int result = Collections.binarySearch(features, feature);

		// If result >= 0 we've found a duplicate and don't add. Otherwise add.
		if (result < 0)
		{
			features.add((-result)-1, feature);
			return true;
		}

		return false;
	}

	public int size()
	{
		return features.size();
	}

	public ArrayList<Feature> getFeatures()
	{
		return features;
	}

	public String getName()
		{ return name; }

	/**
	 * Returns a list of features between the given start and end points
	 * (inclusive). Uses a binary search to locate them as quickly as possible,
	 * using the compare() method defined below.
	 */
	public ArrayList<Feature> getFeatures(int s, int e)
	{
		ArrayList<Feature> list = new ArrayList<Feature>();

		// Start by finding ANY feature that is inside the window
		int index = Collections.binarySearch(features, new Feature(s, e), this);

		if (index >= 0)
		{
			// Then search left to find the FIRST feature within the window
			while (index > 0 && features.get(index-1).getVisualPE() >= s)
				index--;

			// Now add all the features from here until the RHS of the window
			for (int i = index; i < features.size(); i++)
			{
				Feature toAdd = features.get(i);

				if (toAdd.getVisualPS() <= e)
					list.add(toAdd);
				else
					break;
			}
		}

		return list;
	}

	/**
	 * Returns an integer representing the inclusion state of a feature when
	 * compared against the given start and end values for a "window". The
	 * methods returns -1 if it is to the left of it, 0 if any part of it is
	 * within the window, and 1 if it is to the right of it.
	 */
	public int compare(Feature feature, Feature window)
	{
		// RHS of the window...
		if (feature.getVisualPS() > window.getDataPE())
			return 1;

		// LHS of the window...
		if (feature.getVisualPE() < window.getDataPS())
			return -1;

		// Otherwise must be within the window
		return 0;
	}
}