// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data;

import tablet.io.*;

public class BamBam
{
	private BamFileHandler bamHandler;

	// The starting and ending indices (0-indexed) for the current data block
	private int s, e;

	// Size of the block of data (in bases) that we try to load
	private int size;

	BamBam(BamFileHandler bamHandler)
	{
		this.bamHandler = bamHandler;
	}

	public void reset(int newSize)
	{
		size = newSize;

		s = 0;
		e = s + size - 1;
	}

	public void setBlockStart(Contig contig, int startIndex)
	{
		int dataWidth = contig.getDataWidth();

		// Firstly check that the block size we want to load isn't bigger than
		// the total amount of data that could be loaded anyway
		if (size > dataWidth)
			size = dataWidth;
		// Extra safety check
		if (size < 1) size = 1;

		s = startIndex;
		e = s + size - 1;

		// Check we're not overhanging on the lhs of the assembly...
		if (s < 0)
		{
			s = 0;
			e = s + size - 1;
		}
		// ...or the rhs of the assembly.
		else if (e > dataWidth)
		{
			s = dataWidth-size;
			e = s + size - 1;
		}
	}

	public void loadDataBlock(Contig contig)
		throws Exception
	{
		bamHandler.loadDataBlock(contig, s, e);
	}

	public void indexNames()
		throws Exception
	{
		bamHandler.indexNames();
	}

	/**
	 * @return the s
	 */
	public int getS()
	{
		return s;
	}

	/**
	 * @return the e
	 */
	public int getE()
	{
		return e;
	}

	public int getSize()
	{
		return size;
	}

	public void setSize(int size)
		{ this.size = size; }

	public BamFileHandler getBamFileHandler()
		{ return bamHandler; }
}