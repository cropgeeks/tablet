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
		int matePos = read.getMatePos();

		while (high >= low)
		{
			// Get mid while avoiding potential integer overflow
			int mid = low + ((high - low) / 2);
			int startPos = contig.getReads().get(mid).getStartPosition();

			if (startPos < matePos)
				low = mid + 1;

			else if (startPos > matePos)
				high = mid - 1;

			else
				return refinePairSearch(mid, Assembly.getReadName(read), matePos);
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
		String mateName = Assembly.getReadName(mate);

		if (mateName.equals(name))
			return mate;
		else
		{
			Read read = null;
			read = linearSearch(mate, name, pos, mid, -1, mateName);
			if(read != null)
				return read;

			read = linearSearch(mate, name, pos, mid, 1, mateName);
			if(read != null)
				return read;
		}
		return null;
	}

	private Read linearSearch(Read mate, String name, int pos, int mid, int loopModifier, String mateName)
	{
		while (mate != null && pos == mate.getStartPosition() && mid > 0)
		{
			if (mateName.equals(name))
				return mate;

			// Deal with paired end reads which have had pair information encoded in the names of reads
			if (name.endsWith(":1") || name.endsWith(":2"))
			{
				String tempName = name.substring(0, name.length()-2);
				if(mateName.substring(0, mateName.length()-2).equals(tempName))
					return mate;
			}

			mid += loopModifier;
			mate = contig.getReads().get(mid);
			mateName = Assembly.getReadName(mate);
		}
		return null;
	}
}
