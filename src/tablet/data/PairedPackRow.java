package tablet.data;

import java.util.*;

import tablet.gui.*;

public class PairedPackRow extends PackRow
{
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

	protected boolean linesBeforeRead(Read read, int readS, ReadMetaData rmd)
	{
		if (!Prefs.visPaired || !Prefs.visPairLines)
			return false;

		MatedRead matedRead = (MatedRead)read;
		MatedRead mate = matedRead.getMate();

		// if the mate starts before the current read, is mapped and in this contig
		boolean needsLine = matedRead.getMatePos() < readS &&
				matedRead.isMateContig() && rmd.getMateMapped();
		// if the mate is on the same row of the display (the mates don't overlap)
		boolean onDifferentRows = mate != null
				&& mate.getEndPosition() >= matedRead.getStartPosition();

		return needsLine && !onDifferentRows;
	}

	protected boolean linesAfterRead(Read read, int end, ReadMetaData rmd)
	{
		if (!Prefs.visPaired || !Prefs.visPairLines)
			return false;

		MatedRead matedRead = (MatedRead)read;
		MatedRead mate = matedRead.getMate();
		int startPos = matedRead.getStartPosition();

		// if the mate is before the read and the read is offscreen, in this contig
		// and mapped
		boolean needsLine = matedRead.getMatePos() < startPos && startPos > end
				&& matedRead.isMateContig() && rmd.getMateMapped();
		// determine that the reads are in this pack
		boolean samePack = reads.contains(matedRead) && reads.contains(mate);

		return needsLine && samePack && mate != null;
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