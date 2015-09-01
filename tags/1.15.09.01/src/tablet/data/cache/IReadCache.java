// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data.cache;

import tablet.data.*;

/**
 * An interface for accessing read meta data that is stored in a read cache.
 */
public interface IReadCache
{
	public void openForWriting()
		throws Exception;

	public void openForReading()
		throws Exception;

	/** Closes this cache and releases any resources it may be holding. */
	public void close()
		throws Exception;

	/**
	 * Returns the read meta data for the read that matches the given ID.
	 * @return the read meta data for the read that matches the given ID
	 */
	public ReadMetaData getReadMetaData(int id, boolean dataOnly);

	/**
	 * Adds new meta data about a read to the cache.
	 * @param readMetaData the meta data to add
	 */
	public void setReadMetaData(ReadMetaData readMetaData)
		throws Exception;
}