// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data;

import tablet.data.cache.*;

/**
 * A read holds sequence information for a short stretch of sequenced DNA. The
 * Read class only holds the data and its starting position (as aligned against
 * the consenses. All other information is stored in the read cache, with each
 * read accessible via its unique index ID.
 */
public class Read extends Sequence implements Comparable<Read>
{
	// The lookup ID for this read's name (which is stored elsewhere)
	private int id;

	// The position of the beginning of the read, in terms of consensus bases
	private int position;

	/**
	 * Constructs an empty read object
	 */
	public Read()
	{
	}

	/**
	 * Constructs a read with an ID and consensus alignment position.
	 * @param id the id for this read
	 * @param position the position of this read when aligned against the
	 * consensus
	 */
	public Read(int id, int position)
	{
		this.id = id;
		this.position = position;
	}

	int getID()
		{ return id; }
	
	/**
	 * Sets this read's ID.
	 * @param The Read ID.
	 */
	public void setID(int id)
	{
		this.id = id;
	}
	
	/**
	 * Sets this read's start position.
	 * @param this read's start position
	 */
	public void setStartPosition(int position)
		{this.position = position;}

	/**
	 * Returns this read's starting position (aligned against the consensus).
	 * @return this read's starting position
	 */
	public int getStartPosition()
		{ return position; }

	/**
	 * Returns this read's ending position (aligned against the consensus).
	 * @return this read's ending position
	 */
	public int getEndPosition()
		{ return position + length() -1; }

	/** Currently unused (Ace file format related). */
	public void setQAData(int qa_start, int qa_end, int al_start, int al_end)
	{
//		this.qa_start = qa_start;
//		this.qa_end = qa_end;
//		this.al_start = al_start;
//		this.al_end = al_end;
	}

	/**
	 * Compares this read against another. The sort is performed so that reads
	 * will be ordered left-to-right by starting position, with any reads that
	 * start at the same position sub-ordered so that shorted reads are first.
	 */
	public int compareTo(Read other)
	{
		if (position < other.position)
			return -1;
		else if (position > other.position)
			return 1;
		else
		{
			int length = length();
			int oLength = other.length();

			if (length == oLength)
				return 0;
			else if (length < oLength)
				return -1;
			else
				return 1;
		}
	}

	/**
	 * Returns an integer representing the inclusion state of this read when
	 * compared against the given start and end values for a "window". The
	 * methods returns -1 if this read is to the left of it, 0 if any part of
	 * the read is within the window, and 1 if the read is to the right of it.
	 */
	int compareToWindow(int start, int end)
	{
		// RHS of the window...
		if (position > end)
			return 1;

		// LHS of the window...
		if (getEndPosition() < start)
			return -1;

		// Otherwise must be within the window
		return 0;
	}
	
	public Read clone()
	{
		Read newRead = new Read();
		newRead.cloneData(this);
		return newRead;
	}
}