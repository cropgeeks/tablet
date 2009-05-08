package tablet.data;

import java.util.*;

import tablet.data.cache.*;

public class Read extends Sequence implements Comparable<Read>
{
	// The lookup ID for this read's name (which is stored elsewhere)
	private int id;

	// The position of the beginning of the read, in terms of consensus bases
	private int position;

//	private int qa_start, qa_end;
//	private int al_start, al_end;

	public Read()
	{
	}

	public Read(int id, int position)
	{
		this.id = id;
		this.position = position;
	}

	int getID()
		{ return id; }

	public int getStartPosition()
		{ return position; }

	public int getEndPosition()
		{ return position + length() -1; }

	public void setQAData(int qa_start, int qa_end, int al_start, int al_end)
	{
//		this.qa_start = qa_start;
//		this.qa_end = qa_end;
//		this.al_start = al_start;
//		this.al_end = al_end;
	}

	public int compareTo(Read other)
	{
		if (position < other.position)
			return -1;
		else if (position == other.position)
			return 0;
		else
			return 1;
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

	void print(IReadCache cache)
	{
		System.out.println();
		System.out.println("Read " + id + ": " + cache.getReadMetaData(id).getName());
		System.out.print("  length: " + length());
		System.out.print(", position: " + position);

		super.print();

		System.out.println();
	}
}