// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data;

import java.util.*;

import tablet.gui.*;

/**
 * A PackRow represents an ordered collection of reads, from left-to-right in
 * terms of a sequence's nucleotide positions. Each pack forms one "line" of
 * data when viewed in the display.
 */
public class PackRow
{
	protected ArrayList<Read> reads = new ArrayList<Read>();
	private int positionE;

	public void trimToSize()
		{ reads.trimToSize(); }

	/**
	 * Attempts to add the read to this PackRow. It will only be added if it does
	 * not overlap with any reads already stored in this pack.
	 */
	public boolean addRead(Read read)
	{
		if (reads.isEmpty() || read.getStartPosition() > positionE)
		{
			reads.add(read);
			positionE = read.getEndPosition() + Prefs.visPadReads;
			return true;
		}

		return false;
	}

	public LineData getLineData(int start, int end)
	{
		int read = -1;

		// Binary search to find the first read that appears within the window
		int L = 0, M = 0, R = reads.size()-1;

		while (R >= L)
		{
			M = (L+R) / 2;

			int windowed = reads.get(M).compareToWindow(start, end);

			// If this read is within the window
			if (windowed == 0)
			{
				read = M;
				break;
			}

			// LHS of the window
			if (windowed == -1)
				L = M + 1;
			// RHS of the window
			else
				R = M - 1;
		}

		if (read == -1)
			read = M;

		// Search left from this read to find the left-most read that appears in
		// the window (as the binary search will only find the first read that
		// appears anywhere in it).
		for (read = read-1; read >= 0; read--)
			if (reads.get(read).compareToWindow(start, end) == -1)
				break;

		return getLineData(read+1, start, end);
	}

	protected LineData getLineData(int fromRead, int start, int end)
	{
		ReadMetaData[] rmds = new ReadMetaData[end-start+1];
		int[] indexes = new int[end-start+1];

		// Tracking index within the data array
		int dataI = 0;
		// Tracking index on the start->end scale
		int index = start;

		Read read = null;
		ReadMetaData rmd = null;

		ListIterator<Read> itor = reads.listIterator(fromRead);

		while (itor.hasNext())
		{
			read = itor.next();

			if (read.getStartPosition() > end)
				break;

			rmd = Assembly.getReadMetaData(read, true);
			
			int readS = read.getStartPosition();
			int readE = read.getEndPosition();


			// Fill in any bases before the read with blanks
			for (; index < readS; index++, dataI++)
				indexes[dataI] = -1;


			// Fill in the data for this read
			for (; index <= end && index <= readE; index++, dataI++)
			{
				rmds[dataI] = rmd;
				indexes[dataI] = index-readS;
			}
		}

		// Fill bases between the end of the read and the end of the window with
		// blanks
		for (; index <= end; index++, dataI++)
			indexes[dataI] = -1;


		return new LineData(indexes, rmds);
	}

	/**
	 * Returns the read at the given nucleotide position in the pack.
	 */
	Read getReadAt(int position)
	{
		// Binary search to find the read that contains the nucleotide position
		int L = 0, M = 0, R = reads.size()-1;

		while (R >= L)
		{
			M = (L+R) / 2;

			// Position is to the left of this read
			if (position < reads.get(M).getStartPosition())
				R = M - 1;
			// Position is to the right of this read
			else if (position > reads.get(M).getEndPosition())
				L = M + 1;
			// Position must be within this read
			else
				return reads.get(M);
		}

		return null;
	}

	public ArrayList<Read> getReads()
	{
		return reads;
	}
}