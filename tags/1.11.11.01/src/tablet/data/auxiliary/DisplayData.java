// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data.auxiliary;

import java.util.*;

import tablet.data.cache.*;

/**
 * DisplayData holds information about a contig that is *only* required when
 * Tablet is displaying that contig. The information is calculated just before
 * display and discarded afterwards to save memory. The fields of this class are
 * static to assist access from any of the display components.
 */
public class DisplayData
{
	// Contains info to map from an unpadded to a padded position and vice versa
	private static MappingData mappingData;

	// Contains the coverage information across the contig
	private static int[] coverage;
	private static int maxCoverage;
	private static int maxCoverageAtBase;
	private static float averageCoverage;
	private static float averagePercentage;

	/**
	 * Clears the memory allocated for the storage of padded/unpadded mapping
	 * information by this consensus sequence. It is only needed at display time
	 * and if this contig isn't visible it can be a massive waste of memory.
	 */
	public static void clearDisplayData(boolean clearAll)
	{
		if (clearAll)
		{
//			paddedToUnpadded = null;
//			unpaddedToPadded = null;
			mappingData = null;
		}

		coverage = null;
	}

	public static int[] getCoverage()
		{ return coverage; }

	public static int getCoverageAt(int position)
	{
		try { return coverage[position]; }
		catch (Exception e) { return 0; }
	}

	public static void setCoverage(int[] newCoverage)
		{ coverage = newCoverage; }

	public static int getMaxCoverage()
		{ return maxCoverage; }

	public static void setMaxCoverage(int newMaxCoverage)
		{ maxCoverage = newMaxCoverage; }

	public static int getBaseOfMaximum()
		{ return maxCoverageAtBase; }

	public static void setBaseOfMaximum(int newMaxCoverageAtBase)
		{ maxCoverageAtBase = newMaxCoverageAtBase; }

	public static float getAverageCoverage()
		{ return averageCoverage; }

	public static void setAverageCoverage(float newAverageCoverage)
		{ averageCoverage = newAverageCoverage; }

	public static float getAveragePercentage()
		{ return averagePercentage; }

	public static void setAveragePercentage(float newAveragePercentage)
		{ averagePercentage = newAveragePercentage; }


	public static void setPaddedToUnpadded(IArrayIntCache cache)
		{ mappingData.setPaddedToUnpaddedCache(cache); }

	/** Returns true if paddedToUnpadded mapping data is available. */
	public static boolean hasPaddedToUnpadded()
		{ return mappingData != null; }

	/**
	 * Returns the unpadded index (within consensus index space) for the given
	 * padded index position, or -1 if the mapping cannot be made.
	 * @param paddedPosition the padded position to convert to unpadded
	 * @return the unpadded index for the given padded index position
	 */
	public static int paddedToUnpadded(int paddedPosition)
	{
		try {
			return mappingData.getPaddedToUnpadded(paddedPosition);
		}
		catch (Exception e) {
			return -1;
		}
	}

	public static void setUnpaddedToPadded(IArrayIntCache cache)
		{ mappingData.setUnpaddedToPaddedCache(cache); }

	/** Returns true if paddedToUnpadded mapping data is available. */
	public static boolean hasUnpaddedToPadded()
		{ return mappingData != null; }

	/**
	 * Returns the padded index (within consensus index space) for the given
	 * unpadded index position, or -1 if the mapping cannot be made.
	 * @param unpaddedPosition the unpadded position to convert to padded
	 * @return the padded index for the given unpadded index position
	 */
	public static int unpaddedToPadded(int unpaddedPosition)
	{
		try {
			return mappingData.getUnpaddedToPadded(unpaddedPosition);
		}
		catch (Exception e) {
			return -1;
		}
	}

	public static void setMappingData(MappingData newMappingData)
	{
		mappingData = newMappingData;
	}
}