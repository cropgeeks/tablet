// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
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

	/**
	 * Returns a byte array containing sequence information (or -1 for no data)
	 * between the points start and end.
	 */
	byte[] getValues(int start, int end, int scheme)
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

		// If no suitable read was found, just return an array of -1s
//		if (read == -1)
//		{
//			byte[] data = new byte[end-start+1];
//			for (int i = 0; i < data.length; i++)
//				data[i] = -1;
//
//			return data;
//		}

		if (read == -1)
			read = 0;

		// Search left from this read to find the left-most read that appears in
		// the window (as the binary search will only find the first read that
		// appears anywhere in it).
		for (read = read-1; read >= 0; read--)
			if (reads.get(read).compareToWindow(start, end) == -1)
				break;

		return getValues(read+1, start, end, scheme);
	}

	/**
	 * Creates and fills an array with all reads that fit within the given
	 * window start and end positions (starting from the index fromRead).
	 */
	private byte[] getValues(int fromRead, int start, int end, int scheme)
	{
		byte[] data = new byte[end-start+1];

		// Tracking index within the data array
		int dataI = 0;
		// Tracking index on the start->end scale
		int index = start;

		ListIterator<Read> itor = reads.listIterator(fromRead);

		Read read = null;
		while (itor.hasNext())
		{
			read = itor.next();

			MatedRead matedRead = null;

			if(read instanceof MatedRead)
				matedRead = (MatedRead)read;

			if (read.getStartPosition() > end)
				break;

			int readS = read.getStartPosition();
			int readE = read.getEndPosition();

			ReadMetaData rmd = Assembly.getReadMetaData(read, true);

			// Fill in any blanks between the current index the next read
			for (; index < readS; index++, dataI++)
			{
				if(isMate(matedRead, index))
					data[dataI] = 14;
				else
					data[dataI] = -1;
			}

			// Determine color offset
			int color = rmd.getColorSchemeAdjustment(scheme);

			// Fill in the read data
			for (; index <= end && index <= readE; index++, dataI++)
				data[dataI] = (byte) (color + rmd.getStateAt(index-readS));
		}

		MatedRead matedRead = null;
		if(read instanceof MatedRead)
			matedRead = (MatedRead)read;
		// If no more reads are within the window, fill in any blanks between
		// the final read and the end of the array
		for (; index <= end; index++, dataI++)
		{
			if(isMateEndWindow(matedRead, index, end))
				data[dataI] = 14;
			else
				data[dataI] = -1;
		}

		return data;
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

	/**
	 * Checks if the nucleotide position is in the middle of a pair.
	 */
	private boolean isMate(MatedRead pr, int index)
	{
		if(pr == null || pr.getPair() == null)
			return false;
		else
		{
			return (reads.contains(pr) && reads.contains(pr.getPair()) && pr.getStartPosition() > index && pr.getMatePos() < pr.getStartPosition());
		}
	}

	/**
	 * Checks if the nucleotide position is at the end of a screen window and
	 * in the middle of a pair.
	 */
	private boolean isMateEndWindow(MatedRead pr, int index, int end)
	{
		if(pr == null || pr.getPair() == null)
			return false;
		else
		{
			return ((reads.contains(pr) && reads.contains(pr.getPair())) && index > pr.getEndPosition() && pr.getMatePos() > end) ||
					((reads.contains(pr) && reads.contains(pr.getPair())) && index < pr.getStartPosition() && pr.getMatePos() < pr.getStartPosition() && pr.getStartPosition() > end && pr.getMatePos() < end);
		}
	}
}