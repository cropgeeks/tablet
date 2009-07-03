package tablet.data;

/** The consensus sequence for a contig. */
public class Consensus extends Sequence
{
	private byte[] bq;

	// Contains info to map from a padded to an unpadded position
	private int[] paddedToUnpadded;
	// Contains info to map from an unpadded to a padded position
	private int[] unpaddedToPadded;

	/** Constructs a new, empty consensus sequence. */
	public Consensus()
	{
	}

	/**
	 * Ensures that the consensus' internal mappings between padded and unpadded
	 * positions have been properly calculated. Must be called as part of data
	 * loading.
	 */
	public void calculatePaddedMappings()
	{
		calculatePaddedToUnpadded();
		calculateUnpaddedToPadded();
	}

	// Given a padded index value (0 to length-1) what is the unpadded value at
	// that position?
	//
	// A  * T C
	// 0 -1 1 2
	private void calculatePaddedToUnpadded()
	{
		paddedToUnpadded = new int[length()];

		for (int i = 0, index = 0; i < paddedToUnpadded.length; i++)
		{
			if (getStateAt(i) != Sequence.P)
				paddedToUnpadded[i] = index++;

			else
				paddedToUnpadded[i] = -1;
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
	{
		unpaddedToPadded = new int[length()];

		int map = 0;
		for (int i = 0; i < unpaddedToPadded.length; i++)
		{
			if (getStateAt(i) != Sequence.P)
				unpaddedToPadded[map++] = i;
		}

		// Any left over positions can't map to anything
		for (; map < unpaddedToPadded.length; map++)
			unpaddedToPadded[map] = -1;
	}

	/**
	 * Returns the unpadded index (within consensus index space) for the given
	 * padded index position, or -1 if the mapping cannot be made.
	 * @param paddedPosition the padded position to convert to unpadded
	 * @return the unpadded index for the given padded index position
	 */
	public int getUnpaddedPosition(int paddedPosition)
	{
		try {
			return paddedToUnpadded[paddedPosition];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			return -1;
		}
	}

	/**
	 * Returns the padded index (within consensus index space) for the given
	 * unpadded index position, or -1 if the mapping cannot be made.
	 * @param unpaddedPosition the unpadded position to convert to padded
	 * @return the padded index for the given unpadded index position
	 */
	public int getPaddedPosition(int unpaddedPosition)
	{
		try {
			return unpaddedToPadded[unpaddedPosition];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			return -1;
		}
	}

	/**
	 * Sets the base score qualities for this consensus.
	 * @param bq the array of base quality scores (one per base of the consensus
	 * with -1 for bases with no score).
	 */
	public void setBaseQualities(byte[] bq)
		{ this.bq = bq; }

	/**
	 * Returns an array of base quality data, starting at start and ending at
	 * end. These values may be outside of the actual start and end values for
	 * the consensus, in which case -1 will be returned for those positions.
	 * @param start the starting index
	 * @param end the ending index (inclusive)
	 * @return an array of base quality data
	 */
	public byte[] getBaseQualityRange(int start, int end)
	{
		byte[] data = new byte[end-start+1];

		int i = 0, d = 0;
		int length = bq.length;

		// Pre sequence data
		for (i = start; i < 0 && i <= end; i++, d++)
			data[d] = -1;

		// Sequence data
		for (i = i; i <= end && i < length; i++, d++)
			data[d] = bq[i];

		// Post sequence data
		for (i = i; i <= end; i++, d++)
			data[d] = -1;

		return data;
	}

	/**
	 * Returns an array of sequence data, starting at start and ending at end.
	 * These values may be outside of the actual start and end values for the
	 * consensus, in which case -1 will be returned for those positions.
	 * @param start the starting index
	 * @param end the ending index (inclusive)
	 * @return an array of sequence data
	 */
	public byte[] getRange(int start, int end)
	{
		byte[] data = new byte[end-start+1];

		int i = 0, d = 0;
		int length = length();

		// Pre sequence data
		for (i = start; i < 0 && i <= end; i++, d++)
			data[d] = -1;

		// Sequence data
		for (i = i; i <= end && i < length; i++, d++)
			data[d] = getStateAt(i);

		// Post sequence data
		for (i = i; i <= end; i++, d++)
			data[d] = -1;

		return data;
	}

	void print()
	{
		System.out.println();
		System.out.println("Consensus:");
		System.out.println("  length: " + length());

		for (int i = 0; i < length(); i++)
		{
//			System.out.print(DNATable.getDNA(data[i]));
			System.out.print(bq[i] + " ");
		}

		System.out.println();
	}
}