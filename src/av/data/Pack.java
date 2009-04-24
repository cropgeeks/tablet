package av.data;

import java.util.*;

/**
 * A pack represents an ordered collection of reads, from left-to-right in terms
 * of a sequence's nucleotide positions. Each pack forms one "line" of data when
 * viewed in the display.
 */
class Pack
{
	private LinkedList<Read> reads = new LinkedList<Read>();

	private int positionS;
	private int positionE;

	Pack()
	{
	}

	/**
	 * Attempts to add the read to this pack. It will only be added if it does
	 * not overlap with any reads already stored in this pack.
	 */
	boolean addRead(Read read)
	{
		if (reads.size() == 0)
		{
			addReadToList(read);
			return true;
		}

		Read lastRead = reads.get(reads.size()-1);

		if (read.getStartPosition() > lastRead.getEndPosition())
		{
			addReadToList(read);
			return true;
		}

		return false;
	}

	private void addReadToList(Read read)
	{
		// Adds a read to the end of the list. If it's the first read added,
		// then we track its starting position. We also update the ending
		// position (for the pack) to be the ending position of this read.

		if (reads.size() == 0)
			positionS = read.getStartPosition();

		reads.add(read);

		positionE = read.getEndPosition();
	}

	/**
	 * Returns a byte array containing sequence information (or -1 for no data)
	 * between the points start and end.
	 */
	byte[] getValues(int start, int end)
	{
		// TODO: binary (or skip-25) searching to the correct start position
		// TODO: but this would only be suitable with array/vector storage!

		byte[] data = new byte[end-start+1];

		// Tracking index within the data array
		int dataI = 0;
		// Tracking index on the start->end scale
		int index = start;

		// Search for reads that are within the current window (start<->end)
		for (Read read: reads)
		{
			if (read.getEndPosition() < start)
				continue;
			if (read.getStartPosition() > end)
				break;

			int readS = read.getStartPosition();
			int readE = read.getEndPosition();

			int readLength = read.length();

			// Fill in any blanks between the current position and the start of
			// this read
			for (; index < readS; index++, dataI++)
				data[dataI] = -1;

			// Fill in any read data
			for (; index <= end && index <= readE; index++, dataI++)
				data[dataI] = read.getStateAt(index-readS);
		}

		// If no more reads are within the window, fill in any blanks between
		// the final read and the end of the array
		for (; index <= end; index++, dataI++)
			data[dataI] = -1;

		return data;
	}
}