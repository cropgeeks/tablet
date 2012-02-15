// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

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

	protected int windowBinarySearch(int start, int end)
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
				break;

			// LHS of the window
			if (windowed == -1)
				L = M + 1;
			// RHS of the window
			else
				R = M - 1;
		}

		read = M;

		return read;
	}

	LineData getLineData(int start, int end)
	{
		int read = windowBinarySearch(start, end);

		// Search left from this read to find the left-most read that appears in
		// the window (as the binary search will only find the first read that
		// appears anywhere in it).
		for (read = read-1; read >= 0; read--)
			if (reads.get(read).compareToWindow(start, end) == -1)
				break;

		return getLineData(read+1, start, end);
	}

	public static int count = 0;

	// startBase = nucleotide base to start at
	// arraysize = array same size as number of pixels on screen
	// scale = maps number of nucletides to pixels (eg 0.1 = 10 bases per pixel)
	protected LineData getPixelData(int startBase, int arraySize, float scale)
	{
		ReadMetaData[] rmds = new ReadMetaData[arraySize];
		int[] indexes = new int[arraySize];
		Read[] pixReads = new Read[arraySize];

		int prevBase = Integer.MIN_VALUE;
		ReadIndex prevRead = null;

		// the start index within the array we'll be searching from
		int prevReadIndex = -1;

		ReadMetaData d = new ReadMetaData();

		for (int i = 0; i < arraySize; i++)
		{
			// Work out what the nucleotide position is for each pixel point
			int base = startBase + (int)(i / scale);

			// If one base stretches over more than one pixel, then just reuse
			// the data from the last iteration
			if (prevBase == base)
			{
				rmds[i] = rmds[i-1];
				indexes[i] = indexes[i-1];
				pixReads[i] = pixReads[i-1];
			}
			else
			{
				// If the read over the last pixel is also over this pixel...
				if (prevRead != null && prevRead.read.getEndPosition() >= base)
				{
					rmds[i] = rmds[i-1];
					indexes[i] = base-prevRead.read.getStartPosition();
					pixReads[i] = prevRead.read;
				}
				else // we don't yet know what maps to this pixel
				{
					ReadIndex ri = getReadIndexAt(base);
					count++;

					// If it's a read...
					if (ri != null)
					{
						if (scale > 1)
							rmds[i] = Assembly.getReadMetaData(ri.read, true);
						else
							rmds[i] = d;


						// Index (within the read) of its data at this base
						indexes[i] = base-ri.read.getStartPosition();
						pixReads[i] = ri.read;

						prevReadIndex = ri.index;
					}
					// No read to be drawn on this pixel
					else
					{
						int j = i+1;

						// Work out what pixel (if any) the *next* read in the
						// PackRow would be on
						if (++prevReadIndex < reads.size())
						{
							Read nextRead = reads.get(prevReadIndex);
							int s = nextRead.getStartPosition();

							// -1 otherwise the next iteration of loop starts
							// already in the next base and fails to fill the
							// arrays correctly
							int pixel = (int) ((s-startBase) * scale) -1;

							// Next read will be offscreen
							if (pixel >= arraySize)
								j = arraySize;
							else
								j = pixel;
						}
						else
							j = arraySize;

						for (; i < j; i++)
						{
							rmds[i] = null;
							indexes[i] = -1;
							pixReads[i] = null;
						}
					}

					prevRead = ri;
				}
			}

			prevBase = base;
		}

		return new LineData(indexes, rmds, pixReads);
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


		return new LineData(indexes, rmds, null);
	}

	Read getReadAt(int position)
	{
		ReadIndex ri = getReadIndexAt(position);

		if (ri != null)
			return ri.read;

		return null;
	}

	/**
	 * Returns the read at the given nucleotide position in the pack. Can be
	 * passed a starting left index to reduce the size of the array that needs
	 * to be searched (eg, if you're after a read that you know must be to the
	 * RHS of a previously found read at leftIndex-1.
	 */
	private ReadIndex getReadIndexAt(int position)
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
				return new ReadIndex(reads.get(M), M);
		}

		return null;
	}

	public ArrayList<Read> getReads()
	{
		return reads;
	}

	// Simple wrapper around a Read and its position within this PackRow
	private static class ReadIndex
	{
		Read read;
		int index;

		ReadIndex(Read read, int index)
		{
			this.read = read;
			this.index = index;
		}
	}
}