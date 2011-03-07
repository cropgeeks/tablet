// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data;

import java.util.*;

/**
 * Stores information about one row in the PairedStack display. Includes a
 * simple array of reads, with space for two reads.
 */
public class PairedStackRow
{
	private Read readA = null;
	private Read readB = null;

	public void addRead(Read read)
	{
		if(readA == null)
			readA = read;
		else
			readB = read;
	}

	public LineData getLineData(int start, int end)
	{
		ReadMetaData[] reads = new ReadMetaData[end-start+1];
		int[] indexes = new int[end-start+1];

		// Pre-fill array with "blanks"
		Arrays.fill(indexes, -1);

		// Fill in data for first read
		if (readA != null)
			getLineDataForRead(readA, reads, indexes, start, end);

		// Fill in any links between reads
		if (readA != null && readB != null)
			getLineDataForPairLink(indexes, start, end);

		// Fill in the data for the second read
		if (readB != null)
			getLineDataForRead(readB, reads, indexes, start, end);

		return new LineData(indexes, reads);
	}

	// If a read is off screen do nothing, otherwise jump to where it begins and
	// fill the array up to where it ends (or hits the edge of the window)
	private void getLineDataForRead(Read read, ReadMetaData[] reads, int[] indexes, int start, int end)
	{
		int readS = read.getStartPosition();
		int readE = read.getEndPosition();
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
			reads[index - start] = rmd;
			indexes[index - start] = index-readS;
		}
	}

	private void getLineDataForPairLink(int[] indexes, int start, int end)
	{
		int index = readA.getEndPosition();
		int readB_s = readB.getStartPosition();

		if (index < start)
			index = start;

		for (; index <= end && index <= readB_s; index++)
			indexes[index - start] = -2;
	}

	/**
	 * Returns the read at the given position, otherwise returns null
	 */
	Read getReadAt(int position)
	{
		if(readA.getStartPosition() <= position && readA.getEndPosition() >= position)
			return readA;

		else if(readB != null && readB != null && readB.getStartPosition() <= position && readB.getEndPosition() >= position)
			return readB;

		return null;
	}

	public Read[] getPair(int colIndex)
	{
		if(readB == null)
		{
			if(readA instanceof MatedRead)
			{
				MatedRead matedRead = (MatedRead) readA;
				if (matedRead.getPair() != null)
					return new Read [] {readA, matedRead.getPair()};
			}
		}

		return new Read [] {readA, readB};
	}

	public Read[] getPair()
	{
		return new Read[] {readA, readB};
	}
}