package av.data;

import java.util.*;

/**
 * A pack represents an ordered collection of reads, from left-to-right in terms
 * of a sequence's nucleotide positions. Each pack forms one "line" of data when
 * viewed in the display.
 */
public class Pack
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
}