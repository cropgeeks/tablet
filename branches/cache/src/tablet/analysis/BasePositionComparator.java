// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis;

import java.util.*;

import tablet.data.*;

/**
 * Compares each base position of a read against its consensus. Differences
 * between the read and consensus are then encoded back into the read's data,
 * so that these comparisons do not have to be done in real-time when the data
 * is used for rendering.
 */
public class BasePositionComparator
{
	public static void compare(Consensus consensus, Sequence read, int start)
		throws Exception
	{
		byte NOTUSED = Sequence.NOTUSED;

		// Index start position within the consensus sequence
		int c = start;
		int cLength = consensus.length();
		int rLength = read.length();

		for (int r = 0; r < rLength; r++, c++)
		{
			byte value = read.getStateAt(r);

			if (c < 0 || c >= cLength)
			{
				// Out of bounds means that this base on the read does
				// not have a corresponding position on the consensus
				// (and must therefore be different from it)
				read.setStateAt(r, (byte)(value+1));
			}
			else
			{
				// The DNATable encodes its states so that A and dA are
				// only ever 1 byte apart, meaning we can change quickly
				// by just incrementing the value by one
				if (consensus.getStateAt(c) != value && value > NOTUSED)
					read.setStateAt(r, (byte)(value+1));
			}
		}
	}
}