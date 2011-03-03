// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data;

/**
 * Wrapper around the read class to provide easier Paired-End read functionality.
 * Contains simple accessors and mutators for the data fields in the PairedRead
 * class.
 */
public class MatedRead extends Read
{
	private MatedRead pair = null;
	private int matePos;
	private boolean isMateContig;

	public MatedRead(int id, int position)
	{
		super(id, position);
	}

	public MatedRead getPair()
		{ return pair; }

	public void setPair(MatedRead pair)
		{ this.pair = pair; }

	public int getMatePos()
		{ return matePos; }

	public void setMatePos(int matePos)
		{ this.matePos = matePos; }

	public boolean isMateContig()
		{ return isMateContig; }

	public void setIsMateContig(boolean isMateContig)
		{ this.isMateContig = isMateContig; }

}