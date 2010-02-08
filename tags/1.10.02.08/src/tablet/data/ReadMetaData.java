// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
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
	private String name;

	// Is the read complemented or uncomplemented
	private boolean isComplemented;

	private int unpaddedLength;
	private int length;

	private String cigar = "";

	public ReadMetaData()
	{
	}

	public ReadMetaData(String name, boolean isComplemented)
	{
		this.name = name;
		this.isComplemented = isComplemented;
	}

	public ReadMetaData(String name, boolean isComplemented, int unpaddedLength)
	{
		this.name = name;
		this.isComplemented = isComplemented;
		this.unpaddedLength = unpaddedLength;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
		{ return name; }

	public void setComplmented(boolean isComplemented)
	{
		this.isComplemented = isComplemented;
	}

	public boolean isComplemented()
		{ return isComplemented; }

	public int getUnpaddedLength()
		{ return unpaddedLength; }

	/**
	 * Calculates and returns the unpadded length of this sequence. Note: this
	 * information is part of the ReadMetaData class and this method is purely
	 * for calculation to fill that class - it shouldn't be used for any other
	 * purpose.
	 * @return the unpadded length of this sequence
	 */
	public int calculateUnpaddedLength()
	{
		int baseCount = 0;

		for (int i = 0; i < length; i++)
			if (getStateAt(i) != P)
				baseCount++;

		return baseCount;
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
		System.out.println("Length: " + length);
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++)
		{
			byte state = getStateAt(i);
			sb.append(getDNA(state));
		}

		return sb.toString();
	}

	@Override
	public void setData(String sequence)
	{
		super.setData(sequence);
		length = sequence.length();
	}

	public String getCigar()
		{ return cigar; }

	public void setCigar(String cigar)
		{ this.cigar = cigar; }
}