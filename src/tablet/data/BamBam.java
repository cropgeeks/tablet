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

		s = 30000000;
		e = 30100000;
	}

	public void setBlock(int s, int e)
	{
		this.s = s;
		this.e = e;
	}

	public void loadDataBlock(Contig contig)
		throws Exception
	{
		bamHandler.loadData(contig, s, e);
	}
}