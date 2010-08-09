// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data.auxiliary;

import java.util.*;

/**
 * A feature is a region of interest from point pS to pE (inclusive) in
 * consensus space (including offset positions). However, a feature can also be
 * marked by the user as being in unpadded space, which means that Tablet has to
 * translate its pS and pE values from unpadded->padded/display coordinates
 * before they can be used. Because of this, we have two main ways of accessing
 * the position information: getDataPS() which returns the orignal values and
 * getVisualPS which will return a translated value (but only if it has to; if
 * the feature ISN'T unpadded, then the two methods return identical results).
 */
public class Feature implements Comparable<Feature>
{
	// True if features are to use padded coordinates, false if unpadded
	public static boolean ISPADDED = true;

	// Defined "Tablet Types" for supported featured
	public final static byte GFF3 = 0;

	public final static byte ROW_OUTLINE = 100;
	public final static byte COL_OUTLINE = 101;


	// Tracks all known GFF types for the current data set. The hash is cleared
	// by Assembly's constructor whenever new data is imported/loaded.
	public static HashMap<String, String> types = new HashMap<String, String>();
	// Tracks the order for the GFF types
	public static ArrayList<VisibleFeature> order = new ArrayList<VisibleFeature>();

	protected byte tabletType;

	// The GFF3 type of the feature
	protected String gffType;
	// The name of the feature (if it exists)
	protected String name;

	// Start and end position information (original values)
	protected int pS;
	protected int pE;

	// Start and end, converted from unpadded->padded (if required)
	protected Integer u2pS;
	protected Integer u2pE;


	// Used by FeatureTrack to quickly make dummy features to aid in searching
	Feature(int pS, int pE)
	{
		this.pS = pS;
		this.pE = pE;
	}

	public Feature(byte tabletType, String gffType, String name, int pS, int pE)
	{
		this.tabletType = tabletType;

		this.gffType = gffType;
		this.name = name;
		this.pS = pS;
		this.pE = pE;
	}

	public void verifyType()
	{
		// Add this feature's type to the tracking table
		if (types.get(gffType) == null)
		{
			// Auto-add the first three types so that they will be visible
			boolean visible = types.size() < 3;

			types.put(gffType, gffType);
			order.add(new VisibleFeature(gffType, visible));
		}
	}

	public int compareTo(Feature other)
	{
		if (pS < other.pS)
			return -1;

		else if (pS == other.pS)
		{
			if (pE < other.pE)
				return -1;

			else if (pE == other.pE)
			{
				if (gffType.compareTo(other.gffType) < 0)
					return -1;

				else if (gffType.compareTo(other.gffType) == 0)
				{
					if (name.compareTo(other.name) < 0)
						return -1;

					else if (name.compareTo(other.name) == 0)
						return 0;
				}
			}
		}

		return 1;
	}

	public int getTabletType()
		{ return tabletType; }

	public String getGFFType()
		{ return gffType; }

	public String getName()
		{ return name; }

	public static void clearTracking()
	{
		types.clear();
		order.clear();
	}


	/** See class notes above for a description of the following four methods */

	public int getDataPS()
		{ return pS; }

	public int getDataPE()
		{ return pE; }

	/**
	 * Returns the position (in padded-coordinate space) that this feature
	 * starts at, to be used by Tablet when RENDERING this feature. Will always
	 * return the correct position to DRAW at, regardless of data being padded
	 * or unpadded.
	 */
	public int getVisualPS()
	{
		if (ISPADDED)
			return pS;

		// Convert the value from unpadded to padded space (and store it)
		if (u2pS != null)
			return u2pS;

		if (DisplayData.hasUnpaddedToPadded())
			return (u2pS = DisplayData.unpaddedToPadded(pS));

		// If it can't be converted (yet) we'll hopefully get it next time
		else
			return pS;
	}

	public int getVisualPE()
	{
		if (ISPADDED)
			return pE;

		// Convert the value from unpadded to padded space (and store it)
		if (u2pE != null)
			return u2pE;

		if (DisplayData.hasUnpaddedToPadded())
			return (u2pE = DisplayData.unpaddedToPadded(pE));

		// If it can't be converted (yet) we'll hopefully get it next time
		else
			return pE;
	}

	public static class VisibleFeature
	{
		public String type;
		public boolean isVisible;

		public VisibleFeature(String type, boolean isVisible)
		{
			this.type = type;
			this.isVisible = isVisible;
		}
	}
}