// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis;

import tablet.data.*;

/**
 * Determines coverage of a contig. The only method supported so far is the
 * simplest, that of detecting the presence or absense of a read at any given
 * nucleotide position (regardless of how it relates to the consensus).
 */
public class CoverageCalculator extends SimpleJob
{
	private Contig contig;

	// Stores the exact coverage value per base
	private int[] coverage;

	private int maxValue;
	private float averageValue;

	public CoverageCalculator(Contig contig)
	{
		this.contig = contig;
	}

	// TODO: Test case
	public void runJob(int jobIndex)
		throws Exception
	{
		coverage = new int[contig.getWidth()];

		// Maintains a count of the total number of bases across ALL reads
		int baseCount = 0;

		for (Read read: contig.getReads())
		{
			baseCount += read.length();

			int s = read.getStartPosition() + contig.getConsensusOffset();
			int e = read.getEndPosition() + contig.getConsensusOffset();

			// Increment the coverage count for each base covered by this read
			for (int i = s; i <= e; i++)
				coverage[i]++;
		}

		averageValue = baseCount / (float) contig.getWidth();

		// Finally, work out what the maximum depth of coverage was
		for (int i: coverage)
			if (i > maxValue)
				maxValue = i;
	}

	public int[] getCoverage()
		{ return coverage; }

	public int getMaximum()
		{ return maxValue; }

	public float getAverage()
		{ return averageValue; }
}