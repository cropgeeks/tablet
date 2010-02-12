// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data;

import tablet.io.*;

public class BamBam
{
	private BaiFileHandler bamHandler;

	// The starting and ending indices (0-indexed) for the current data block
	private int s, e;

	BamBam(BaiFileHandler bamHandler)
	{
		this.bamHandler = bamHandler;

		s = 10000;
		e = 10200;
	}

	public void setBlockStart(Contig contig, int s)
	{
		System.out.println(s);
		s = s-50;
		e = s+9050;
		
		int cLength = contig.getConsensus().length();
		if(s >= 0 && s < cLength)
			this.s = s;
		else
			throw new RuntimeException("BAM S Invalid: " + s);

		if(e >= 0 && e < cLength)
			this.e = e;
		else
			throw new RuntimeException("BAM E Invalid: " + e);
	}

	public void loadDataBlock(Contig contig)
		throws Exception
	{
		bamHandler.loadData(contig, getS(), getE());
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
}