// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
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
	protected ArrayList<Read> reads = new ArrayList<>();
	private int positionE;

	public void trimToSize()
		{ reads.trimToSize(); }

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

	// startBase = nucleotide base to start at
	// arraysize = array same size as number of pixels on screen
	// scale = maps number of nucletides to pixels (eg 0.1 = 10 bases per pixel)
	protected LineData getPixelData(int startBase, int arraySize, float scale, boolean getMetaData)
	{
		ReadMetaData[] rmds = new ReadMetaData[arraySize];
		int[] indexes = new int[arraySize];
		Read[] pixReads = new Read[arraySize];

		int prevBase = Integer.MIN_VALUE;
		ReadIndex prevRead = null;

		// the start index within the array we'll be searching from
//		int prevReadIndex = -1;

		for (int i = 0; i < arraySize; i++)
		{
			// Work out what the nucleotide position is for each pixel point
			int currBase = startBase + (int)(i / scale);

			// If one base stretches over more than one pixel, then just reuse
			// the data from the last iteration
			if (prevBase == currBase)
			{
				rmds[i] = rmds[i-1];
				indexes[i] = indexes[i-1];
				pixReads[i] = pixReads[i-1];
			}
			else
			{
				// If the read over the last pixel is also over this pixel...
				if (prevRead != null && prevRead.read.e() >= currBase)
				{
					rmds[i] = rmds[i-1];
					indexes[i] = currBase-prevRead.read.s();
					pixReads[i] = prevRead.read;
				}
				else // we don't yet know what maps to this pixel
				{
					ReadIndex rIndex = getReadIndexAt(currBase);

					// If it's a read...
					if (rIndex != null)
					{
						if (scale >= 1 || getMetaData)
							rmds[i] = Assembly.getReadMetaData(rIndex.read, true);

						// Index (within the read) of its data at this base
						indexes[i] = currBase-rIndex.read.s();
						pixReads[i] = rIndex.read;

//						prevReadIndex = rIndex.index;
					}
					// No read to be drawn on this pixel
					// Optimisations commented out as they don't quite work
					else
					{
//						int j = arraySize;
//
//						// Work out what pixel (if any) the *next* read in the
//						// PackRow would be on
//						if (++prevReadIndex < reads.size())
//						{
//							Read nextRead = reads.get(prevReadIndex);
//							int s = nextRead.s();
//							int pixel = (int) ((s-startBase) * scale);
//
//							// Next read will be onscreen
//							if (pixel < arraySize)
//							{
//								if (pixel > i)
//									j = pixel;
//								else
//									j = pixel + 1;
//							}
//							//System.out.println("pixel: " + pixel);
//						}
//
//						//System.out.println("i: " + i);
//						//System.out.println("j: " + j);
//
//						// Fill every pixel with blanks
//						for (; i < j; i++)
//						{
//							rmds[i] = null;
							indexes[i] = -1;
//							pixReads[i] = null;
//						}
//
//						// Set the main loop counter i to 1 pixel prior to this
//						// new position (-1 relates to the i++ in the main loop)
//						i = j - 1;
					}

					prevRead = rIndex;
				}
			}

			prevBase = currBase;
		}

		return new LineData(indexes, rmds, pixReads);
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
	protected ReadIndex getReadIndexAt(int position)
	{
		// Binary search to find the read that contains the nucleotide position
		int L = 0, M = 0, R = reads.size()-1;

		while (R >= L)
		{
			M = (L+R) / 2;

			// Position is to the left of this read
			if (position < reads.get(M).s())
				R = M - 1;
			// Position is to the right of this read
			else if (position > reads.get(M).e())
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

	public int getPositionE()
		{ return positionE; }

	public void setPositionE(int positionE)
		{ this.positionE = positionE; }

	// Simple wrapper around a Read and its position within this PackRow
	protected static class ReadIndex
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