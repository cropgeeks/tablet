package tablet.data;

import java.util.*;

/**
 * A pack represents an ordered collection of reads, from left-to-right in terms
 * of a sequence's nucleotide positions. Each pack forms one "line" of data when
 * viewed in the display.
 */
public class Pack
{
	private ArrayList<Read> reads = new ArrayList<Read>();

	// TODO: This isn't being used yet - it should be possible to speed up
	// searches with it though
	private int positionS;
	private int positionE;

	public void trimToSize()
		{ reads.trimToSize(); }

	/**
	 * Attempts to add the read to this pack. It will only be added if it does
	 * not overlap with any reads already stored in this pack.
	 */
	public boolean addRead(Read read)
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
		if (read == -1)
		{
			byte[] data = new byte[end-start+1];
			for (int i = 0; i < data.length; i++)
				data[i] = -1;

			return data;
		}

		// Search left from this read to find the left-most read that appears in
		// the window (as the binary search will only find the first read that
		// appears anywhere in it).
		for (read = read-1; read >= 0; read--)
			if (reads.get(read).compareToWindow(start, end) == -1)
				break;

		return getValues(read+1, start, end);
	}

	/**
	 * Creates and fills an array with all reads that fit within the given
	 * window start and end positions (starting from the index fromRead).
	 */
	private byte[] getValues(int fromRead, int start, int end)
	{
		byte[] data = new byte[end-start+1];

		// Tracking index within the data array
		int dataI = 0;
		// Tracking index on the start->end scale
		int index = start;

		ListIterator<Read> itor = reads.listIterator(fromRead);

		while (itor.hasNext())
		{
			Read read = itor.next();

			if (read.getStartPosition() > end)
				break;

			int readS = read.getStartPosition();
			int readE = read.getEndPosition();

			// Fill in any blanks between the current index the next read
			for (; index < readS; index++, dataI++)
				data[dataI] = -1;

			// Fill in the read data
			for (; index <= end && index <= readE; index++, dataI++)
				data[dataI] = read.getStateAt(index-readS);
		}

		// If no more reads are within the window, fill in any blanks between
		// the final read and the end of the array
		for (; index <= end; index++, dataI++)
			data[dataI] = -1;

		return data;
	}

	Read getReadAt(int position)
	{
		int read = -1;

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
}