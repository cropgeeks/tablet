package tablet.data;

public class ReadNameData
{
	private String name;
	private int unpaddedLength;
	private String cigar;
	private String mateContig;

	public ReadNameData()
	{
	}

	public ReadNameData(String name)
	{
		this.name = name;
	}

	public ReadNameData(String name, int unpaddedLength, String cigar, String mateContig)
	{
		this.name = name;
		this.unpaddedLength = unpaddedLength;
		this.cigar = cigar;
		this.mateContig = mateContig;
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

}
