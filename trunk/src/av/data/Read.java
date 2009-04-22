package av.data;

import java.util.*;

import av.data.cache.*;

public class Read extends Sequence implements Comparable<Read>
{
	// The lookup ID for this read's name (which is stored elsewhere)
	private int id;

	// Is the read complemented or uncomplemented
	private boolean complemented;

	// The position of the beginning of the read, in terms of consensus bases
	private int position;

//	private int qa_start, qa_end;
//	private int al_start, al_end;

	public Read()
	{
	}

	public Read(int id, boolean complemented, int position)
	{
		this.id = id;
		this.complemented = complemented;
		this.position = position;
	}


	public boolean getComplemented()
		{ return complemented; }

//	public void setComplemented(boolean complemented)
//		{ this.complemented = complemented; }

	public int getStartPosition()
		{ return position; }

	public int getEndPosition()
		{ return position + length() -1; }

//	public void setPosition(int position)
//		{ this.position = position; }

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


	void print(IReadCache cache)
	{
		System.out.println();
		System.out.println("Read " + id + ": " + cache.getName(id));
		System.out.print("  length: " + length());
		System.out.print(", position: " + position);
		System.out.println(", complemented: " + complemented);

		super.print();

		System.out.println();
	}
}