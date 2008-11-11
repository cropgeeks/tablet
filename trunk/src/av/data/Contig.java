package av.data;

import java.util.*;

public class Contig
{
	private String name;
	private boolean complemented;

	private Vector<Read> reads;

	public Contig()
	{
	}

	public Contig(String name, boolean complemented, int readCount)
	{
		this.name = name;
		this.complemented = complemented;

		reads = new Vector<Read>(readCount);
	}

	public String getName()
		{ return name; }

//	public void setName(String name)
//		{ this.name = name; }

	public boolean getComplemented()
		{ return complemented; }

//	public void setComplemented(boolean complemented)
//		{ this.complemented = complemented; }

	public Vector<Read> getReads()
		{ return reads; }
}