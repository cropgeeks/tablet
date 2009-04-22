package av.analysis;

import av.data.*;

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
		byte UNKNOWN = Sequence.UNKNOWN;

		long count = 0;

		for (Contig contig: assembly.getContigs())
		{
			Consensus consensus = contig.getConsensus();

			for (Read read: contig.getReads())
			{
				// Index start position within the consensus sequence
				int c = read.getStartPosition();
				int length = read.length();

				for (int r = 0; r < length; r++, c++)
				{
					// See http://stackoverflow.com/questions/141560/should-trycatch-go-inside-or-outside-a-loop

					// There's not much in it, but the try/catch does seem ever
					// so slightly faster than a manual check for out of bounds

					byte value = read.getStateAt(r);

					try
					{
						// The DNATable encodes its states so that A and dA are
						// only ever 1 byte apart, meaning we can change quickly
						// by just incrementing the value by one
						if (consensus.getStateAt(c) != value && value > UNKNOWN)
							read.setStateAt(r, (byte)(value+1));
					}
					catch (ArrayIndexOutOfBoundsException e)
					{
						// Out of bounds means that this base on the read does
						// not have a corresponding position on the consensus
						// (and must therefore be different from it)
						read.setStateAt(r, (byte)(value+1));
					}

					count++;
				}
			}
		}

		System.out.println("Ran " + count + " base comparisons");
	}
}