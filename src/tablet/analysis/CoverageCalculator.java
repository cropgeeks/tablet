// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.analysis;

import scri.commons.gui.*;

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
		coverage = new int[contig.getVisualWidth()];

		// Maintains a count of the total number of bases across ALL reads
		long baseCount = 0;
		int vS = contig.getVisualStart();

		for (Read read: contig.getReads())
		{
			int s = read.s() - vS;
			int e = read.e() - vS;

			// Extra checks that may happen with BAM (but shouldn't with non-BAM)
			if (s < 0)
				s = 0;
			if (e >= coverage.length)
				e = coverage.length-1;

			// Increment the coverage count for each base covered by this read
			for (int i = s; i <= e; i++)
			{
				coverage[i]++;
				baseCount++;
			}
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

		averageCoverage = baseCount / (float) coverage.length;
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