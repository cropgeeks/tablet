// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data;

/**
 * Represents and holds additional meta data about a read. Chances are this is
 * data that was cached elsewhere (eg on disk) because it uses too much memory
 * to store this meta data inside the actual Read class. It can be held here
 * because it isn't needed at all times and can be fetched from the cache as and
 * when it *is* needed.
 */
public class ReadMetaData extends Sequence
{
	public static final int UNPAIRED = 0;
	public static final int FIRSTINP = 1;
	public static final int SECNDINP = 2;
	public static final int ORPHANED = 3;

	private int length;
	private int numberInPair;

	// Is the read complemented or uncomplemented
	private boolean isComplemented;
	private boolean isPaired;
	// True if this read's mate has been mapped to a contig in the assembly
	private boolean mateMapped;

	public ReadMetaData()
	{
	}

	public ReadMetaData(boolean isComplemented)
	{
		this.isComplemented = isComplemented;
	}

	public void setComplmented(boolean isComplemented)
	{
		this.isComplemented = isComplemented;
	}

	public boolean isComplemented()
		{ return isComplemented; }

	/**
	 * Calculates and returns the unpadded length of this sequence. Note: this
	 * information is part of the ReadMetaData class and this method is purely
	 * for calculation to fill that class - it shouldn't be used for any other
	 * purpose.
	 */
	public int calculateUnpaddedLength()
	{
		int unpaddedLength = 0;

		for (int i = 0; i < length; i++)
			if (getStateAt(i) != P)
				unpaddedLength++;

		return unpaddedLength;
	}

	public int length()
	{
		return length;
	}

	public void setLength(int length)
	{
		this.length = length;
	}

	public boolean getIsPaired()
	{
		return isPaired;
	}

	public void setIsPaired(boolean isPaired)
	{
		this.isPaired = isPaired;
	}

	public boolean getMateMapped()
	{
		return mateMapped;
	}

	public void setMateMapped(boolean mateMapped)
	{
		this.mateMapped = mateMapped;
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

	public int getNumberInPair()
		{ return numberInPair; }

	public void setNumberInPair(int numberInPair)
	{
		this.numberInPair = numberInPair;
	}

	public int getPairedType()
	{
		if (numberInPair == 1 && mateMapped)
			return FIRSTINP;

		else if (numberInPair == 2 && mateMapped)
			return SECNDINP;

		else if (isPaired && !mateMapped)
			return ORPHANED;

		else
			return UNPAIRED;
	}
}