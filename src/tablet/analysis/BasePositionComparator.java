package tablet.analysis;

import tablet.data.*;

/**
 * Given an assembly, this class will compare every read in each contig against
 * that contig's consensus sequence. Comparing each base position, differences
 * between the read and consensus are then encoded back into the read's data,
 * so that these comparisons do not have to be done in real-time when the data
 * is used for rendering.
 */
public class BasePositionComparator
{
	private Assembly assembly;

	public BasePositionComparator(Assembly assembly)
	{
		this.assembly = assembly;
	}

	// TODO: Test case
	public void doComparisons()
	{
		byte NOTUSED = Sequence.NOTUSED;

		long count = 0;

		for (Contig contig: assembly.getContigs())
		{
			Consensus consensus = contig.getConsensus();

			for (Read read: contig.getReads())
			{
				// Index start position within the consensus sequence
				int c = read.getStartPosition();
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

					count++;
				}
			}
		}

		System.out.println("Ran " + count + " base comparisons");
	}
}