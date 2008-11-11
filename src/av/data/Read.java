package av.data;

import java.util.*;

public class Read extends Sequence implements Comparable<Read>
{
	private boolean complemented;
	private int position;

	private int qa_start, qa_end;
	private int al_start, al_end;

	public Read()
	{
	}

	public Read(String name, boolean complemented, int position)
	{
		this.name = name.getBytes();
		this.complemented = complemented;
		this.position = position;
	}


	public boolean getComplemented()
		{ return complemented; }

//	public void setComplemented(boolean complemented)
//		{ this.complemented = complemented; }

	public int getPosition()
		{ return position; }

//	public void setPosition(int position)
//		{ this.position = position; }


	public int compareTo(Read other)
	{
		if (position < other.position)
			return -1;
		else if (position == other.position)
			return 0;
		else
			return 1;
	}
}