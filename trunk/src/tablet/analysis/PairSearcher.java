package tablet.analysis;

import tablet.data.*;

public class PairSearcher
{
	private Contig contig;

	public PairSearcher(Contig contig)
	{
		this.contig = contig;
	}

	/**
	 * Binary search for a read's pair within the current contig (or current
	 * bam window of the contig in the case of paired-end bam).
	 */
	public Read search(MatedRead read)
	{
		int low = 0;
		int high = contig.getReads().size() - 1;

		while (high >= low)
		{
			// Get mid while avoiding potential integer overflow
			int mid = low + ((high - low) / 2);
			int startPos = contig.getReads().get(mid).getStartPosition();

			if (startPos < read.getMatePos())
				low = mid + 1;

			else if (startPos > read.getMatePos())
				high = mid - 1;

			else
				return refinePairSearch(mid, Assembly.getReadNameData(read).getName(), read.getMatePos());
		}
		return null;
	}

	/**
	 * Binary search for a read's pair within the current contig (or current
	 * bam window of the contig in the case of paired-end bam).
	 */
	public Read searchForPair(String name, int pos)
	{
		int low = 0;
		int high = contig.getReads().size() - 1;

		while (high >= low)
		{
			// Get mid while avoiding potential integer overflow
			int mid = low + ((high - low) / 2);
			int startPos = contig.getReads().get(mid).getStartPosition();

			if (startPos < pos)
				low = mid + 1;

			else if (startPos > pos)
				high = mid - 1;

			else
				return refinePairSearch(mid, name, pos);
		}
		return null;
	}

	/**
	 * There is a potential for more than one read mapping to a position, as
	 * such we need to refine the search by searching linearly in both directions.
	 */
	private Read refinePairSearch(int mid, String name, int pos)
	{
		Read mate = contig.getReads().get(mid);
		ReadNameData pairRnd = Assembly.getReadNameData(mate);

		if (pairRnd.getName().equals(name))
			return mate;
		else
		{
			Read read = null;
			read = linearSearch(mate, name, pos, mid, -1, pairRnd);
			if(read != null)
				return read;

			read = linearSearch(mate, name, pos, mid, 1, pairRnd);
			if(read != null)
				return read;
		}
		return null;
	}

	private Read linearSearch(Read pr, String name, int pos, int mid, int loopModifier, ReadNameData pairRnd)
	{
		while (pr != null && pos == pr.getStartPosition())
		{
			if (pairRnd.getName().equals(name))
				return pr;

			mid += loopModifier;
			pr = contig.getReads().get(mid);
			pairRnd = Assembly.getReadNameData(pr);
		}
		return null;
	}
}
