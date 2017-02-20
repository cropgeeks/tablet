// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data;

public class PairedPackRow extends PackRow
{
	// startBase = nucleotide base to start at
	// arraysize = array same size as number of pixels on screen
	// scale = maps number of nucletides to pixels (eg 0.1 = 10 bases per pixel)
	@Override
	protected LineData getPixelData(int startBase, int arraySize, float scale, boolean getMetaData)
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

						if ((scale >= 1 || getMetaData) && isLink == false)
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

	// Check to see if there is a MateLink object at the given position. If so,
	// determine if it has genuine Read objects to it left and right sides in
	// the data list and then return them.
	Read[] getPairForLinkPosition(int colIndex)
	{
		ReadIndex link = getReadIndexAt(colIndex);

		if (link == null || link.read.isNotMateLink())
			return null;
		if (link.index == 0 || link.index == reads.size()-1)
			return null;

		Read[] pair = new Read[] { reads.get(link.index-1), reads.get(link.index+1) };

		return pair;
	}
}