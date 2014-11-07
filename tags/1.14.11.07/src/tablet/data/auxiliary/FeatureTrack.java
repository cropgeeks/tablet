// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data.auxiliary;

import java.util.*;

import scri.commons.*;

/**
 * Simple class to encapsulate a list of features (on one "track").
 */
public class FeatureTrack implements Comparator<Feature>
{
	private String name;

	// Store features in an interval tree so that they can be searched and
	// overlapping features can be found easily.
	private IntervalTree<Feature> tree = new IntervalTree<>();

	public FeatureTrack(String name)
		{ this.name = name; }

	/**
	 * Adds a feature to the interval tree (a modified binary search tree).
	 * Interval trees make it easy to find all the features which can be found
	 * by given start and end points.
	 */
	public void addFeature(Feature feature)
	{
		tree.add(feature, feature.getVisualPS(), feature.getVisualPE());
	}

	public String getName()
		{ return name; }

	/**
	 * Returns an ArrayList of features between the given start and end points
	 * (inclusive). Uses an interval tree search to locate the required features.
	 */
	public ArrayList<Feature> getFeatures(int s, int e)
	{
		return tree.intervalSearch(s, e);
	}

	/**
	 * Returns an integer representing the inclusion state of a feature when
	 * compared against the given start and end values for a "window". The
	 * methods returns -1 if it is to the left of it, 0 if any part of it is
	 * within the window, and 1 if it is to the right of it.
	 */
	@Override
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