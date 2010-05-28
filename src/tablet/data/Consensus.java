// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data;

/** The consensus sequence for a contig. */
public class Consensus extends Sequence
{
	// Base quality information, one byte per nucleotide base
	private byte[] bq;

	private int unpaddedLength;
	private int length;

	/** Constructs a new, empty consensus sequence. */
	public Consensus()
	{
	}

	/**
	 * Calculates the unpadded length of this sequence.
	 */
	public void calculateUnpaddedLength()
	{
		unpaddedLength = 0;

		for (int i = 0; i < length; i++)
			if (getStateAt(i) != P)
				unpaddedLength++;
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
	 * Returns true if this consensus sequence contains quality scores for its
	 * bases.
	 */
	public boolean hasBaseQualities()
		{ return bq != null; }

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

	public int length()
	{
		return length;
	}

	public void setLength(int length)
	{
		this.length = length;
	}

	/**
	 * Returns a string representation of this sequence.
	 * @return a string representation of this thread
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++)
		{
			byte state = getStateAt(i);
			sb.append(getDNA(state));
		}

		return sb.toString();
	}

	@Override
	public void setData(StringBuilder sequence)
	{
		super.setData(sequence);
		length = sequence.length();
	}
}