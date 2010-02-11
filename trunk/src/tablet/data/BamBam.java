// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data;

import tablet.io.*;

public class BamBam
{
	private BAIFileReader bamReader;

	// The starting and ending indices (0-indexed) for the current data block
	private int s, e;

	BamBam(BAIFileReader bamReader)
	{
		this.bamReader = bamReader;

		s = 0;
		e = 100;
	}

	public void setBlock(int s, int e)
	{
		this.s = s;
		this.e = e;
	}

	public void loadDataBlock(Contig contig)
		throws Exception
	{
		bamReader.loadData(contig, s, e);
	}
}