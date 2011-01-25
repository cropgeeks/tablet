// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data.cache;

import java.io.*;
import java.nio.*;

/**
 * Tracks and maintains the index (on disk) to a FileCache object.
 */
public class ArrayLongFileCache extends TabletCache
{
	private int length = 0;

	public ArrayLongFileCache(File cacheFile)
	{
		super.cacheFile = cacheFile;
	}

	public int length()
		{ return length; }

	public long getValue(int index)
		throws IOException
	{
		rnd.seek(index * 8);

		return rnd.readLongFromBuffer();
	}

	public void addValue(long value)
		throws IOException
	{
		out.writeLong(value);
		length++;
	}
}