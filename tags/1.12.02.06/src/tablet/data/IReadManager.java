// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data;

import java.util.ArrayList;

/**
 * An interface for accessing read data that is required for visual display.
 */
public interface IReadManager
{
	public int size();

	/** Returns the read at the given position, or null if no read is there. */
	public Read getReadAt(int line, int nucleotidePosition);

	public int getLineForRead(Read read);

	public ArrayList<Read> getReadNames(int startIndex, int endIndex);

	public ArrayList<Read> getLine(int line);

	/**
	 * Returns an array (one element per base) with each element containing a
	 * reference to the ReadMetaData object at each base, for the given line
	 * between the points start and end.
	 */
	public LineData getLineData(int line, int start, int end);

	/**
	 * Returns the pair of reads that form each end of the (possible) link line
	 * at the given position. Returns null if no link line is at that position.
	 */
	public Read[] getPairForLink(int rowIndex, int colIndex);
}