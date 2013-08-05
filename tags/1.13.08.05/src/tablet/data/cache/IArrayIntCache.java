// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data.cache;

import java.io.*;

/**
 * An interface for accessing int[] data stored in a cache
 */
public interface IArrayIntCache
{
	public void openForWriting()
		throws Exception;

	public void openForReading()
		throws Exception;

	/** Closes this cache and releases any resources it may be holding. */
	public void close()
		throws Exception;

	public int length()
		throws Exception;

	/** Retrieves a value from the cache at the given index location. */
	public int getValue(int index)
		throws Exception;

	/** Adds a new value to the cache. */
	public void addValue(int value)
		throws Exception;
}