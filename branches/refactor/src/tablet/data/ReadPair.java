// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data;

import tablet.gui.Prefs;

/**
 * Stores information about one row in the PairedStack display. Includes a
 * simple array of reads, with space for two reads.
 */
public class ReadPair
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

		// Tracking index within the data array
		int dataI = 0;
		// Tracking index on the start->end scale
		int index = start;

		Read [] pair = new Read [] {readA, readB};

		// loop over the reads in the data window
		for(int i=0; i < pair.length; i++)
		{
			Read read = pair[i];

			if(read == null || read.getStartPosition() > end)
				break;

			int readS = read.getStartPosition();
			int readE = read.getEndPosition();

			ReadMetaData rmd = Assembly.getReadMetaData(read, true);

			if(read instanceof MatedRead && Prefs.visPairLines)
			{
				boolean drawLine = drawMateLineBeforeRead(read, readS, rmd);

				for (; index < readS; index++, dataI++)
				{
					// Is this an area between the start of each read in a pair of reads
					if(drawLine)
						indexes[dataI] = -2;
					else
						indexes[dataI] = -1;
				}
			}
			else
				for (; index < readS; index++, dataI++)
					indexes[dataI] = -1;

			// Fill in the read data
			for (; index <= end && index <= readE; index++, dataI++)
			{
				indexes[dataI] = index-readS;
				reads[dataI] = rmd;
			}
		}

		if (index <= end)
		{

			Read read = pair[0];

			if(read instanceof MatedRead && Prefs.visPairLines)
			{
				MatedRead matedRead = (MatedRead)read;

				// Work out these variables in advance rather than on each iteration
				int endPos = matedRead.getEndPosition();
				int matePos = matedRead.getMatePos();
				boolean isMateContig = matedRead.isMateContig();
				boolean needsLine = matePos > end && isMateContig && matedRead.getPair() != null;

				// If no more reads are within the window, fill in any blanks between
				// the final read and the end of the array
				for (; index <= end; index++, dataI++)
				{
					if(index > endPos && needsLine)
						indexes[dataI] = -2;
					else
						indexes[dataI] = -1;
				}
			}
			else
				for (; index <= end; index++, dataI++)
					indexes[dataI] = -1;
		}

		return new LineData(indexes, reads);
	}

	private boolean drawMateLineBeforeRead(Read read, int readS, ReadMetaData rmd)
	{
		MatedRead matedRead = (MatedRead) read;
		// Work out these variables in advance rather than on each iteration
		int startPos = matedRead.getStartPosition();
		int matePos = matedRead.getMatePos();
		boolean isMateContig = matedRead.isMateContig();
		MatedRead mate = matedRead.getPair();
		int mateEndPos = 0;
		if (mate != null)
			mateEndPos = mate.getEndPosition();
		boolean needsLine = matePos < readS && isMateContig && rmd.getMateMapped();
		boolean onDifferentRows = mate != null && mateEndPos >= startPos;
		boolean drawLine = needsLine & !onDifferentRows;
		return drawLine;
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

	public Read [] getPair(int colIndex)
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