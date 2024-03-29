// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data.auxiliary;

import java.awt.*;
import java.net.*;
import java.util.*;

import tablet.gui.*;

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
	public static final String SNP = "SNP";
	public static final String CIGAR_I = "CIGAR-I";
	public static final String CIGAR_D = "CIGAR-D";
	public static final String CIGAR_SKIPPED = "CIGAR-N";
	public static final String CIGAR_LEFT_CLIP = "CIGAR-LEFT-CLIP";
	public static final String CIGAR_RIGHT_CLIP = "CIGAR-RIGHT-CLIP";

	// True if features are to use padded coordinates, false if unpadded
	public static boolean ISPADDED = true;

	// Tracks all known GFF types for the current data set. The hash is cleared
	// by Assembly's constructor whenever new data is imported/loaded.
	public static HashMap<String, String> types = new HashMap<>();
	// Tracks the order for the GFF types
	public static ArrayList<VisibleFeature> order = new ArrayList<>();
	// And their colours
	public static HashMap<String, Color> colors = new HashMap<>();

	// The GFF3 type of the feature
	protected String gffType;
	// The name of the feature (if it exists)
	protected String name;

	protected String[] tags;

	// Start and end position information (original values)
	protected int pS;
	protected int pE;

	// Start and end, converted from unpadded->padded (if required)
	protected int u2pS;
	protected boolean u2pSDefined;

	protected int u2pE;
	protected boolean u2pEDefined;


	public Feature(String gffType, String name, int pS, int pE)
	{
		this.gffType = gffType;
		this.name = name;
		this.pS = pS;
		this.pE = pE;
	}

	public void setTags(String[] tags)
		{ this.tags = tags; }

	public String getTagsAsHTMLString()
	{
		if (tags == null)
			return "";

		StringBuilder str = new StringBuilder();
		for (String tag: tags)
		{
			try { str.append(URLDecoder.decode(tag, "UTF-8") + "<br>"); }
			catch (Exception e) {}
		}

		return str.toString();
	}

	public String getTagsAsString()
	{
		if (tags == null)
			return "";

		String lb = System.getProperty("line.separator");

		StringBuilder str = new StringBuilder();
		for (String tag: tags)
		{
			try { str.append(URLDecoder.decode(tag, "UTF-8")).append(lb); }
			catch (Exception e) {}
		}

		return str.toString();
	}

	public void verifyType()
	{
		// Add this feature's type to the tracking table
		if (types.get(gffType) == null)
		{
			// Auto-add the first three types so that they will be visible
			boolean visible = types.size() < 3 || autoAddCigar();

			types.put(gffType, gffType);
			order.add(new VisibleFeature(gffType, visible));

			int seed = 100;
			int hash = gffType.hashCode() + seed;
			colors.put(gffType, WebsafePalette.getColor(hash));
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
		if (u2pSDefined)
			return u2pS;

		if (DisplayData.hasUnpaddedToPadded())
		{
			u2pSDefined = true;
			return (u2pS = DisplayData.unpaddedToPadded(pS));
		}

		// If it can't be converted (yet) we'll hopefully get it next time
		else
			return pS;
	}

	public int getVisualPE()
	{
		if (ISPADDED)
			return pE;

		// Convert the value from unpadded to padded space (and store it)
		if (u2pEDefined)
			return u2pE;

		if (DisplayData.hasUnpaddedToPadded())
		{
			u2pEDefined = true;
			return (u2pE = DisplayData.unpaddedToPadded(pE));
		}

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

	// A collection of 216 "web safe" colors
	private static class WebsafePalette
	{
		private static ArrayList<Color> colors;

		static
		{
			colors = new ArrayList<Color>();

			for (int r = 0; r < 256; r += 51)
				for (int g = 0; g < 256; g += 51)
					for (int b = 0; b < 256; b += 51)
					{
						// Skip certain colours that we know are no good. This
						// blocks out the last two columns (the lighter colours)
						if (b >= 153)
							continue;

						colors.add(new Color(r, g, b));
					}
		}

		public static int getColorCount()
			{ return colors.size(); }

		public static Color getColor(int index)
		{
			if (index < 0)
				index *= -1;

			return colors.get(index % colors.size());
		}
	}

	// Checks to see whether or not we should automatically create feature tracks for CIGAR features in BAM / SAM files
	private boolean autoAddCigar()
	{
		return Prefs.visAutoCreateCigarTracks && gffType.equals(CIGAR_I) || gffType.equals(CIGAR_D)
			|| gffType.equals(CIGAR_LEFT_CLIP) || gffType.equals(CIGAR_RIGHT_CLIP);
	}
}