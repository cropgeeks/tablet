// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data;

/**
 * Wrapper class that holds the information needed to draw a "line" of data on
 * screen. It's basically two arrays (one element per on-screen base), with one
 * array holding references to the reads at each base, and the other holding
 * the index positions of those reads (so their actual values can be read).
 */
public class LineData
{
	public static final int EMPTY = -1;
	public static final int PAIRLINK = -2;

	private int[] indexes;
	private ReadMetaData[] rmds;
	private Read[] reads;

	LineData(int[] indexes, ReadMetaData[] rmds, Read[] reads)
	{
		this.indexes = indexes;
		this.rmds = rmds;
		this.reads = reads;
	}

	public int[] getIndexes()
		{ return indexes; }

	public ReadMetaData[] getRMDs()
		{ return rmds; }

	public Read[] getReads()
		{ return reads; }
}