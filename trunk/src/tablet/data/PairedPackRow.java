// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data;

import java.util.*;

public class PairedPackRow extends PackRow
{
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
					if (prevRead.read instanceof MateLink == false)
					{
						rmds[i] = rmds[i-1];
						indexes[i] = currBase-prevRead.read.s();
						pixReads[i] = prevRead.read;
					}
					else
						indexes[i] = -2;
				}
				else // we don't yet know what maps to this pixel
				{
					ReadIndex rIndex = getReadIndexAt(currBase);

					// If it's a read...
					if (rIndex != null)
					{
						boolean isLink = (rIndex.read instanceof MateLink);

						if (scale >= 1 && isLink == false)
							rmds[i] = Assembly.getReadMetaData(rIndex.read, true);

						// If we don't have a mate link fill data as normal
						// otherwise use -2 to indicate the base is in a MateLink
						if (isLink == false)
						{
							// Index (within the read) of its data at this base
							indexes[i] = currBase-rIndex.read.s();
							pixReads[i] = rIndex.read;
						}
						else
							indexes[i] = -2;
					}
					// No read to be drawn on this pixel
					// Optimisations commented out as they don't quite work
					else
					{
							indexes[i] = -1;
					}

					prevRead = rIndex;
				}
			}

			prevBase = currBase;
		}

		return new LineData(indexes, rmds, pixReads);
	}

	@Override
	protected LineData getLineData(int fromRead, int start, int end)
	{
		ReadMetaData[] rmds = new ReadMetaData[end-start+1];
		int[] indexes = new int[end-start+1];

		// Pre-fill the array; means we only have to fill in read data or link lines
		Arrays.fill(indexes, -1);

		// Without this step you can run into situations where loop knows
		// nothing about the read to the left of the window, potentially
		// breaking link line drawing
		Read prev = fromRead > 0 ? reads.get(fromRead-1) : null;

		ListIterator<Read> itor = reads.listIterator(fromRead);

		while (itor.hasNext())
		{
			Read read = itor.next();

			if (read instanceof MatedRead)
			{
				Read mate = ((MatedRead)read).getMate();
				// if the previous read equals the current read's mate draw a link line
				if (prev != null && prev == mate)
					getLineDataForPairLink(read, prev, indexes, start, end);
			}

			// Fills in the arrays with the correct data for the read
			getLineDataForRead(read, rmds, indexes, start, end);

			prev = read;
		}

		return new LineData(indexes, rmds, null);
	}

	// If a read is off screen do nothing, otherwise jump to where it begins and
	// fill the array up to where it ends (or hits the edge of the window)
	private void getLineDataForRead(Read read, ReadMetaData[] rmds, int[] indexes, int start, int end)
	{
		int readS = read.s();
		int readE = read.e();
		int index = start;

		// No point doing anything for reads which aren't on screen
		if (readE < start || readS > end)
			return;

		// Skip empty bases before the read actually starts
		if (readS >= start && readS <= end)
			index = readS;

		ReadMetaData rmd = Assembly.getReadMetaData(read, true);

		for (; index <= end && index <= readE; index++)
		{
			rmds[index - start] = rmd;
			indexes[index - start] = index-readS;
		}
	}

	private void getLineDataForPairLink(Read read, Read prev, int[] indexes, int start, int end)
	{
		int prevE = prev.e()+1;
		int readS = read.s();

		if (prevE < start)
			prevE = start;

		for (; prevE <= end && prevE <= readS; prevE++)
			indexes[prevE - start] = -2;
	}

	Read[] getPairForLinkPosition(int colIndex)
	{
		int index = windowBinarySearch(colIndex, colIndex);

		MatedRead read = (MatedRead) reads.get(index);

		if (read.getMate() != null)
		{
			Read[] pair = new Read[] { read, read.getMate() };

			// This ensures the reads are always in the correct left/right order
			Arrays.sort(pair);

			return pair;
		}

		return null;
	}
}