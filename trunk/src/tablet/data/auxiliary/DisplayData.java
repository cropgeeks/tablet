package tablet.data.auxiliary;

import tablet.analysis.*;
import tablet.data.*;

/**
 * DisplayData holds information about a contig that is *only* required when
 * Tablet is displaying that contig. The information is calculated just before
 * display and discarded afterwards to save memory. The fields of this class are
 * static to assist access from any of the display components.
 */
public class DisplayData
{
	// Contains info to map from a padded to an unpadded position
	private static int[] paddedToUnpadded;
	// Contains info to map from an unpadded to a padded position
	private static int[] unpaddedToPadded;

	// Contains the coverage information across the contig
	private static int[] coverage;
	private static int maxCoverage;
	private static float averageCoverage;

	public static void calculateData(Contig contig)
	{
		try
		{
			BaseMappingCalculator bm = new BaseMappingCalculator(contig.getConsensus());
			bm.runJob(0);

			paddedToUnpadded = bm.getPaddedToUnpaddedArray();
			unpaddedToPadded = bm.getUnpaddedToPaddedArray();

			CoverageCalculator cc = new CoverageCalculator(contig);
			cc.runJob(0);
			coverage = cc.getCoverage();
			maxCoverage = cc.getMaximum();
			averageCoverage = cc.getAverage();
		}
		catch (Exception e) {}
	}

	/**
	 * Clears the memory allocated for the storage of padded/unpadded mapping
	 * information by this consensus sequence. It is only needed at display time
	 * and if this contig isn't visible it can be a massive waste of memory.
	 */
	public static void clearData()
	{
		paddedToUnpadded = null;
		unpaddedToPadded = null;

		coverage = null;
	}

	public static int[] getCoverage()
		{ return coverage; }

	public static int getMaxCoverage()
		{ return maxCoverage; }

	public static float getAverageCoverage()
		{ return averageCoverage; }

	/**
	 * Returns the unpadded index (within consensus index space) for the given
	 * padded index position, or -1 if the mapping cannot be made.
	 * @param paddedPosition the padded position to convert to unpadded
	 * @return the unpadded index for the given padded index position
	 */
	public static int getUnpaddedPosition(int paddedPosition)
	{
		try {
			return paddedToUnpadded[paddedPosition];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			return -1;
		}
	}

	/**
	 * Returns the padded index (within consensus index space) for the given
	 * unpadded index position, or -1 if the mapping cannot be made.
	 * @param unpaddedPosition the unpadded position to convert to padded
	 * @return the padded index for the given unpadded index position
	 */
	public static int getPaddedPosition(int unpaddedPosition)
	{
		try {
			return unpaddedToPadded[unpaddedPosition];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			return -1;
		}
	}
}