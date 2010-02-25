// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis;

import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.data.cache.*;

/**
 * Calculates the padded->unpadded and unpadded->padded translation information
 * for a contig's consensus sequence. Note: although this class extends from
 * SimpleJob it doesn't currently supply progress or maximum details as it runs
 * (mainly because it's so fast it's not worth it).
 */
public class BaseMappingCalculator extends BackgroundJob
{
	private Consensus c;

	// Contains info to map from a padded to an unpadded position
	private IArrayIntCache paddedToUnpadded;
	// Contains info to map from an unpadded to a padded position
	private IArrayIntCache unpaddedToPadded;

	public BaseMappingCalculator(Consensus c, IArrayIntCache paddedToUnpadded, IArrayIntCache unpaddedToPadded)
	{
		this.c = c;
		this.paddedToUnpadded = paddedToUnpadded;
		this.unpaddedToPadded = unpaddedToPadded;
	}

	public Boolean call()
		throws Exception
	{
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		Thread.currentThread().setName("BaseMappingCalculator");

		// Because the BaseMappingCalculator (might) be accessing files, it's
		// best to let any previous instances completely finish before starting
		if (previous != null)
			while (previous.isRunning)
				try { Thread.sleep(50); }
				catch (InterruptedException e) {}

		paddedToUnpadded.openForWriting();
		unpaddedToPadded.openForWriting();

		calculatePaddedToUnpadded();
		calculateUnpaddedToPadded();

		if (okToRun)
		{
			paddedToUnpadded.openForReading();
			unpaddedToPadded.openForReading();

			DisplayData.setPaddedToUnpadded(paddedToUnpadded);
			DisplayData.setUnpaddedToPadded(unpaddedToPadded);
		}
		else
		{
			paddedToUnpadded.close();
			unpaddedToPadded.close();
		}

		// Remove any references to objects that were in use (to free memory)
		c = null;
		paddedToUnpadded = null;
		unpaddedToPadded = null;

		cleanup();

		return true;
	}


	// Given a padded index value (0 to length-1) what is the unpadded value at
	// that position?
	//
	// A  * T C
	// 0 -1 1 2
	private void calculatePaddedToUnpadded()
		throws Exception
	{
		int length = c.length();

		for (int i = 0, index = 0; i < length && okToRun; i++)
		{
			if (c.getStateAt(i) != Sequence.P)
				paddedToUnpadded.addValue(index++);

			else
				paddedToUnpadded.addValue(-1);
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
		throws Exception
	{
		int length = c.length();
		int map = 0;

		for (int i = 0; i < length && okToRun; i++)
		{
			if (c.getStateAt(i) != Sequence.P)
			{
				unpaddedToPadded.addValue(i);
				map++;
			}
		}

		// Any left over positions can't map to anything
		for (; map < length && okToRun; map++)
			unpaddedToPadded.addValue(-1);
	}
}