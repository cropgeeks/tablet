package tablet.analysis;

import tablet.data.*;

/**
 * Determines coverage of a contig. The only method supported so far is the
 * simplest, that of detecting the presence or absense of a read at any given
 * nucleotide position (regardless of how it relates to the consensus).
 */
public class CoverageCalculator
{
	private Contig contig;

	// Stores the exact coverage value per base
	public static int[] coverage;
	// Stores values for a moving window average over all the bases
	private int[] movAverage;

	private int maxValue;
	private float averageValue;

	public CoverageCalculator(Contig contig)
	{
		this.contig = contig;
	}

	public int getMaximum()
		{ return maxValue; }

	public float getAverage()
		{ return averageValue; }

	public int[] getCoverage()
	{
		long s = System.currentTimeMillis();
		calculateReadCoverage();
		long e = System.currentTimeMillis();

		System.out.println("CoverageCalculator: " + (e-s) + "ms");

		return coverage;
	}

	// TODO: Test case
	private void calculateReadCoverage()
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

	// TODO: There may be other methods of determining "coverage"
	private void calculateConsensusSupport()
	{
	}

	public int[] getMovingAverage(int windowSize)
	{
		return movAverage;
	}
}