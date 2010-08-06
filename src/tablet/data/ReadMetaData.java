// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data;

import static tablet.gui.viewer.colors.ColorScheme.*;

/**
 * Represents and holds additional meta data about a read. Chances are this is
 * data that was cached elsewhere (eg on disk) because it uses too much memory
 * to store this meta data inside the actual Read class. It can be held here
 * because it isn't needed at all times and can be fetched from the cache as and
 * when it *is* needed.
 */
public class ReadMetaData extends Sequence
{
	// Is the read complemented or uncomplemented
	private boolean isComplemented;

	private int length;

	private int numberInPair;

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

	/**
	 * Returns the offset amount to be added to any base's state value within
	 * this read to ensure it matches a value held by the color scheme in use.
	 * EG, 'A' (forward) may have a value of 5 but 5 (+16) for 'A' (reverse)
	 */
	public int getColorSchemeAdjustment(int scheme)
	{
		switch (scheme)
		{
			case DIRECTION: return isComplemented ? 16:0;

			case READTYPE:
			{
				switch (numberInPair)
				{
					case 1: return 16;
					case 2: return 32;
				}
			}
		}

		return 0;
	}
}