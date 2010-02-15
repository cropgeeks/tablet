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
	private int maxValueAt;
	private float averageCoverage;
	private float averagePercentage;

	public CoverageCalculator(Contig contig)
	{
		this.contig = contig;
	}

	// TODO: Test case
	public void runJob(int jobIndex)
		throws Exception
	{
		coverage = new int[contig.getWidth()];

		// TODO-BAM
		if(true)
			return;

		// Maintains a count of the total number of bases across ALL reads
		int baseCount = 0;

		for (Read read: contig.getReads())
		{
			baseCount += read.length();

			int s = read.getStartPosition() + contig.getVisualStart();
			int e = read.getEndPosition() + contig.getVisualStart();

			// Increment the coverage count for each base covered by this read
			for (int i = s; i <= e; i++)
				coverage[i]++;
		}

		int basesCovered = 0;

		// Finally, work out what the maximum depth of coverage was, the average
		// percentage coverage, and the base with the maximum depth
		for (int i = 0; i < coverage.length; i++)
		{
			if (coverage[i] > maxValue)
			{
				maxValue = coverage[i];
				maxValueAt = i;
			}

			if (coverage[i] > 0)
				basesCovered++;
		}

		averageCoverage = baseCount / (float) contig.getWidth();
		averagePercentage = (basesCovered / (float) coverage.length) * 100;
	}

	public int[] getCoverage()
		{ return coverage; }

	public int getMaximum()
		{ return maxValue; }

	public int getBaseOfMaximum()
		{ return maxValueAt; }

	public float getAverageCoverage()
		{ return averageCoverage; }

	public float getAveragePercentage()
		{ return averagePercentage; }
}