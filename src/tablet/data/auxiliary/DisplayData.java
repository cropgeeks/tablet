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
	// Reference to the contig that this DisplayData holds information for
	private static Contig contig;

	// Contains info to map from a padded to an unpadded position
	private static int[] paddedToUnpadded;
	// Contains info to map from an unpadded to a padded position
	private static int[] unpaddedToPadded;

	// Contains the coverage information across the contig
	private static int[] coverage;
	private static int maxCoverage;

	// TODO: Thread this off (and make everything check that data is available
	// before attempting to access it)
	public static void calculateData(Contig contig)
	{
		long s = System.currentTimeMillis();

		calculatePaddedToUnpadded(contig.getConsensus());
		calculateUnpaddedToPadded(contig.getConsensus());

		CoverageCalculator cc = new CoverageCalculator(contig);
		coverage = cc.getCoverage();
		maxCoverage = cc.getMaximum();

		long e = System.currentTimeMillis();
		System.out.println("DisplayData: " + (e-s) + "ms");
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

	// Given a padded index value (0 to length-1) what is the unpadded value at
	// that position?
	//
	// A  * T C
	// 0 -1 1 2
	private static void calculatePaddedToUnpadded(Consensus c)
	{
		paddedToUnpadded = new int[c.length()];

		for (int i = 0, index = 0; i < paddedToUnpadded.length; i++)
		{
			if (c.getStateAt(i) != Sequence.P)
				paddedToUnpadded[i] = index++;

			else
				paddedToUnpadded[i] = -1;
		}
	}

	// Given an unpadded index value (0 to length-1) what index within the real
	// data array does that map back to? In other words, given the first
	// unpadded value (unpadded index=0), where does this lie = padded 0 (the
	// A). Given the second unpadded value (unpadded index=1), this time it maps
	// to the T, which is padded value 2.
	// A * T  C
	// 0 2 3 -1
	private static void calculateUnpaddedToPadded(Consensus c)
	{
		unpaddedToPadded = new int[c.length()];

		int map = 0;
		for (int i = 0; i < unpaddedToPadded.length; i++)
		{
			if (c.getStateAt(i) != Sequence.P)
				unpaddedToPadded[map++] = i;
		}

		// Any left over positions can't map to anything
		for (; map < unpaddedToPadded.length; map++)
			unpaddedToPadded[map] = -1;
	}

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