package tablet.data;

public class ReadNameData
{
	private String name, cigar, mateContig;
	private int unpaddedLength, insertSize, numberInPair;
	private boolean isProperPair;

	public ReadNameData()
	{
	}

	public ReadNameData(String name)
	{
		this.name = name;
	}

	public ReadNameData(String name, int unpaddedLength, String cigar, String mateContig, int insertSize, boolean isProperPair, int numberInPair)
	{
		this.name = name;
		this.unpaddedLength = unpaddedLength;
		this.cigar = cigar;
		this.mateContig = mateContig;
		this.insertSize = insertSize;
		this.isProperPair = isProperPair;
		this.numberInPair = numberInPair;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
		{ return name; }

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
