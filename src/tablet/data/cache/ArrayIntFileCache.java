// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data.cache;

import java.io.*;
import java.util.concurrent.*;

import scri.commons.io.*;

public class ArrayIntFileCache extends TabletCache implements IArrayIntCache
{
	private int length = 0;

	public ArrayIntFileCache(File cacheFile)
	{
		super.cacheFile = cacheFile;
	}

	public int length()
		{ return length; }

	public int getValue(int index)
		throws IOException
	{
		rnd.seek(index * 4);

		return rnd.readIntFromBuffer();
	}

	public void addValue(int value)
		throws IOException
	{
		out.writeInt(value);
		length++;
	}
}