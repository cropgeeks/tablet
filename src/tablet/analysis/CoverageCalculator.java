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

	private int[] coverage;
	private int max;
	private float average;

	public CoverageCalculator(Contig contig)
	{
		this.contig = contig;
	}

	public int[] getCoverage()
		{ return coverage; }

	public int getMaximum()
		{ return max; }

	public float getAverage()
		{ return average; }

	// TODO: Test case
	public void runJob(int jobIndex)
	{
		long s = System.currentTimeMillis();
		calculateReadCoverage();
		long e = System.currentTimeMillis();

		System.out.println("CoverageCalculator: " + (e-s) + "ms");
	}

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

		average = baseCount / (float) contig.getWidth();

		// Finally, work out what the maximum depth of coverage was
		for (int i: coverage)
			if (i > max)
				max = i;
	}

	// TODO: There may be other methods of determing "coverage"
	private void calculateConsensusSupport()
	{
	}
}