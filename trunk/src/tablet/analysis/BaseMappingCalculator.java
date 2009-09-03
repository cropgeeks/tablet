// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis;

import tablet.data.*;

/**
 * Calculates the padded->unpadded and unpadded->padded translation information
 * for a contig's consensus sequence. Note: although this class extends from
 * SimpleJob it doesn't currently supply progress or maximum details as it runs
 * (mainly because it's so fast it's not worth it).
 */
public class BaseMappingCalculator extends SimpleJob
{
	private Consensus c;

	// Contains info to map from a padded to an unpadded position
	private int[] paddedToUnpadded;
	// Contains info to map from an unpadded to a padded position
	private int[] unpaddedToPadded;

	public BaseMappingCalculator(Consensus c)
	{
		this.c = c;
	}

	public int[] getPaddedToUnpaddedArray()
		{ return paddedToUnpadded; }

	public int[] getUnpaddedToPaddedArray()
		{ return unpaddedToPadded; }

	public void runJob(int jobIndex)
		throws Exception
	{
		calculatePaddedToUnpadded();
		calculateUnpaddedToPadded();
	}

	// Given a padded index value (0 to length-1) what is the unpadded value at
	// that position?
	//
	// A  * T C
	// 0 -1 1 2
	private void calculatePaddedToUnpadded()
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
	private void calculateUnpaddedToPadded()
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
}