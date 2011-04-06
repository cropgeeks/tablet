// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data;

public class ReadNameData
{
	private String name, namePostfix, cigar, mateContig;
	private int unpaddedLength, insertSize, numberInPair;
	private boolean isProperPair;

	public ReadNameData()
	{
	}

	public ReadNameData(String fullName)
	{
		if (fullName.endsWith(":1") || fullName.endsWith(":2") ||
			fullName.endsWith("/1") || fullName.endsWith("/2"))
		{
			namePostfix = fullName.substring(fullName.length()-2);
			name = fullName.substring(0, fullName.length()-2);
		}
		else
		{
			name = fullName;
			namePostfix = "";
		}
	}

	public ReadNameData(String name, String namePostfix, int unpaddedLength, String cigar, String mateContig, int insertSize, boolean isProperPair, int numberInPair)
	{
		this.name = name;
		this.namePostfix = namePostfix;
		this.unpaddedLength = unpaddedLength;
		this.cigar = cigar;
		this.mateContig = mateContig;
		this.insertSize = insertSize;
		this.isProperPair = isProperPair;
		this.numberInPair = numberInPair;
	}

	public String getNamePrefix()
		{ return name; }

	public String getName()
		{ return name + namePostfix; }

	public String getNamePostfix()
		{ return namePostfix; }

	public void setUnpaddedLength(int unpaddedLength)
	{
		this.unpaddedLength = unpaddedLength;
	}

	public int getUnpaddedLength()
		{ return unpaddedLength; }

	public void setCigar(String cigar)
	{
		this.cigar = cigar;
	}

	public String getCigar()
		{ return cigar; }

	public String getMateContig()
		{ return mateContig; }

	public void setMateContig(String mateContig)
	{
		this.mateContig = mateContig;
	}

	public int getInsertSize()
		{ return insertSize; }

	public void setInsertSize(int insertSize)
	{
		this.insertSize = insertSize;
	}

	public boolean isProperPair()
		{ return isProperPair; }

	public void setIsProperPair(boolean isProperPair)
	{
		this.isProperPair = isProperPair;
	}

	public int getNumberInPair()
		{ return numberInPair; }

	public void setNumberInPair(int numberInPair)
	{
		this.numberInPair = numberInPair;
	}

}