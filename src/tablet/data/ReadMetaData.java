// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data;

import tablet.gui.viewer.colors.*;

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
	public static final int DFRSTINP = 4;
	public static final int DSCNDINP = 5;

	private int length;
	private byte numberInPair;

	// Is the read complemented or uncomplemented
	private boolean isComplemented;
	private boolean isPaired;
	// True if this read's mate has been mapped to a contig in the assembly
	private boolean mateMapped;
	private boolean isMateContig;
	private short readGroup;

	// Caches information about the length of this read (in memory cache only)
	// that is used to determine which colour bin will be used to draw this read
	// when using the read length colour scheme
	private int lengthBin;

	public ReadMetaData()
	{
	}

	public ReadMetaData(boolean isComplemented)
	{
		this.isComplemented = isComplemented;
	}

	public void setComplemented(boolean isComplemented)
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

		lengthBin = ReadLengthScheme.getBin(length);
	}

	public boolean getIsPaired()
	{
		return isPaired;
	}

	public int getMismatches()
	{
		int count = 0;

		for (int i = 0; i < length; i++)
			if (getStateAt(i) % 2 == 1)
				count++;

		return count;
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

	public byte getNumberInPair()
		{ return numberInPair; }

	public void setNumberInPair(byte numberInPair)
	{
		this.numberInPair = numberInPair;
	}

	public int getPairedType()
	{
		if (mateMapped && isMateContig)
		{
			if (numberInPair == 1)
				return FIRSTINP;
			else
				return SECNDINP;
		}

		else if (mateMapped && !isMateContig)
		{
			if (numberInPair == 1)
				return DFRSTINP;
			else
				return DSCNDINP;
		}

		else if (isPaired && !mateMapped)
			return ORPHANED;

		else
			return UNPAIRED;
	}

	public short getReadGroup()
		{ return readGroup; }

	public void setReadGroup(short readGroup)
		{ this.readGroup = readGroup; }

	public int getLengthBin()
		{ return lengthBin; }

	public boolean isMateContig()
		{ return isMateContig; }

	public void setIsMateContig(boolean isMateContig)
		{ this.isMateContig = isMateContig; }
}