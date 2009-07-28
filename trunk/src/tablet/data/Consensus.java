package tablet.data;

/** The consensus sequence for a contig. */
public class Consensus extends Sequence
{
	// Base quality information, one byte per nucleotide base
	private byte[] bq;

	// Contains info to map from a padded to an unpadded position
	private int[] paddedToUnpadded;
	// Contains info to map from an unpadded to a padded position
	private int[] unpaddedToPadded;

	private int unpaddedLength;

	/** Constructs a new, empty consensus sequence. */
	public Consensus()
	{
	}

	/**
	 * Sets the unpadded length of this consensus sequence. This is done by
	 * calling Sequence.calculateUnpaddedLength() and storing the result.
	 * @return the unpadded length of this consensus sequence
	 */
	public int calculateUnpaddedLength()
		{ return (unpaddedLength = super.calculateUnpaddedLength()); }

	/**
	 * Ensures that the consensus' internal mappings between padded and unpadded
	 * positions have been properly calculated. Must be called before display.
	 */
	public void calculatePaddedMappings()
	{
		calculatePaddedToUnpadded();
		calculateUnpaddedToPadded();
	}

	/**
	 * Clears the memory allocated for the storage of padded/unpadded mapping
	 * information by this consensus sequence. It is only needed at display time
	 * and if this contig isn't visible it can be a massive waste of memory.
	 */
	public void clearPaddedMappings()
	{
		paddedToUnpadded = null;
		unpaddedToPadded = null;
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
	 * Returns the unpadded length of this consensus sequence.
	 * @return the unpadded length of this consensus sequence
	 */
	public int getUnpaddedLength()
		{ return unpaddedLength; }

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
}