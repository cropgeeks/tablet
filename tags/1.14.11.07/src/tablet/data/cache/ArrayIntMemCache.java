// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data.cache;

import java.io.*;

public class ArrayIntMemCache implements IArrayIntCache
{
	private int[] data;
	private int pointer = 0;

	public ArrayIntMemCache(int size)
	{
		data = new int[size];
	}

	public void openForWriting()
		throws IOException
	{}

	public void openForReading()
		throws IOException
	{}

	public void close()
		throws IOException
	{
		// Clear any used memory
		data = null;
	}

	public int length()
		{ return data.length; }

	public int getValue(int index)
		throws ArrayIndexOutOfBoundsException
	{
		return data[index];
	}

	public void addValue(int value)
		throws ArrayIndexOutOfBoundsException
	{
		data[pointer] = value;
		pointer++;
	}
}