// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data;

import java.util.*;

import tablet.gui.*;

/**
 * A pack represents an ordered collection of reads, from left-to-right in terms
 * of a sequence's nucleotide positions. Each pack forms one "line" of data when
 * viewed in the display.
 */
public class Pack
{
	private ArrayList<Read> reads = new ArrayList<Read>();
	private int positionE;

	public void trimToSize()
		{ reads.trimToSize(); }

	/**
	 * Attempts to add the read to this pack. It will only be added if it does
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

	private LineData getLineData(int fromRead, int start, int end)
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
			rmd = Assembly.getReadMetaData(read, true);

			if (read.getStartPosition() > end)
				break;

			int readS = read.getStartPosition();
			int readE = read.getEndPosition();


			// Decide what to put in the bases before each read
			if (read instanceof MatedRead && linesBeforeRead(read, readS, rmd))
				for (; index < readS; index++, dataI++)
					indexes[dataI] = -2;
			else
				for (; index < readS; index++, dataI++)
					indexes[dataI] = -1;


			// Fill in the data for this read
			for (; index <= end && index <= readE; index++, dataI++)
			{
				rmds[dataI] = rmd;
				indexes[dataI] = index-readS;
			}
		}

		// If no more reads are within the window, fill in any blanks between
		// the final read and the end of the array
		if (read instanceof MatedRead && linesAfterRead(read, end, rmd))
			for (; index <= end; index++, dataI++)
				indexes[dataI] = -2;
		else
			for (; index <= end; index++, dataI++)
				indexes[dataI] = -1;


		return new LineData(indexes, rmds);
	}

	private boolean linesBeforeRead(Read read, int readS, ReadMetaData rmd)
	{
		if (!Prefs.visPaired || !Prefs.visPairLines)
			return false;

		MatedRead matedRead = (MatedRead)read;
		MatedRead mate = matedRead.getPair();

		// if the mate starts before the current read, is mapped and in this contig
		boolean needsLine = matedRead.getMatePos() < readS &&
				matedRead.isMateContig() && rmd.getMateMapped();
		// if the mate is on the same row of the display (the mates don't overlap)
		boolean onDifferentRows = mate != null
				&& mate.getEndPosition() >= matedRead.getStartPosition();

		return needsLine && !onDifferentRows;
	}

	private boolean linesAfterRead(Read read, int end, ReadMetaData rmd)
	{
		if (!Prefs.visPaired || !Prefs.visPairLines)
			return false;

		MatedRead matedRead = (MatedRead)read;
		MatedRead mate = matedRead.getPair();
		int startPos = matedRead.getStartPosition();

		// if the mate is before the read and the read is offscreen, in this contig
		// and mapped
		boolean needsLine = matedRead.getMatePos() < startPos && startPos > end
				&& matedRead.isMateContig() && rmd.getMateMapped();
		// determine that the reads are in this pack
		boolean samePack = reads.contains(matedRead) && reads.contains(mate);

		return needsLine && samePack && mate != null;
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

	/**
	 * Returns the pair at the given nucleotide position in the pack.
	 */
	public Read[] getPair(int colIndex)
	{
		for(int i=reads.size()-1; i >= 0; i--)
		{
			Read read = reads.get(i);
			if(read.getStartPosition() <= colIndex)
				return getPair(read);
		}
		return null;
	}

	/**
	 * Provides the body for the for loop in getPair(int coIndex).
	 */
	private Read[] getPair(Read read)
	{
		Read[] pair = new Read[2];
		pair[0] = read;

		if (read instanceof MatedRead)
		{
			MatedRead pr = (MatedRead) read;
			if (pr.getPair() != null)
				pair[1] = pr.getPair();
			else
				pair[1] = null;

			return pair;
		}
		else
			return null;
	}

	public ArrayList<Read> getReads()
	{
		return reads;
	}
}