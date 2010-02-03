// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data.cache;

import java.io.*;
import java.util.concurrent.*;

import tablet.data.*;

import scri.commons.file.*;

/**
 * Concrete implementation of the IDataCache interface that stores its data in
 * a binary file on disk. The number of bytes written per element is stored in
 * an accompanying index file (see FileCacheIndex), allowing for random
 * read-back from any location with a fixed access time.
 */
public class ReadFileCache extends TabletCache implements IReadCache
{
	private ReadFileCacheIndex index;

	// When writing, how many bytes have been written to the cache?
	private long byteCount = 0;

	public ReadFileCache(File cacheFile, File indexFile)
	{
		this.cacheFile = cacheFile;
		index = new ReadFileCacheIndex(indexFile);
	}

	public void openForWriting()
		throws IOException
	{
		super.openForWriting();
		index.openForWriting();
	}

	public void openForReading()
		throws IOException
	{
		super.openForReading();
		index.openForReading();
	}

	public void close()
		throws IOException
	{
		super.close();
		index.close();
	}

	public ReadMetaData getReadMetaData(int id)
	{
		while (true)
		{
			try
			{
				ReadMetaData rmd = null;

				fileLock.acquire();

				try
				{
					long seekTo = index.getSeekPosition(id);
					rnd.seek(seekTo);

					// Read the length (in bytes) that the name takes up
					int length = rnd.readIntFromBuffer();

					// Make an array of this length
					byte[] array = new byte[length];
					// Then read its name from the file
					rnd.read(array);
					String name = new String(array, "UTF8");

					// Then C or U
					boolean isComplemented = rnd.readBooleanFromBuffer();

					// Unpadded length
					int unpaddedLength = rnd.readIntFromBuffer();

					int dataLength = rnd.readIntFromBuffer();

					byte[] data;
					if(dataLength % 2 == 0)
						data = new byte[dataLength/2];
					else
						data = new byte[(dataLength/2)+1];
					rnd.read(data);

					rmd = new ReadMetaData(name, isComplemented, unpaddedLength);
					rmd.setRawData(data);
					rmd.setLength(dataLength);
				}
				catch (Exception e)	{
					e.printStackTrace();
				}

				fileLock.release();
				return rmd;
			}
			catch (InterruptedException e) {}
		}
	}

	public void setReadMetaData(ReadMetaData readMetaData)
		throws Exception
	{
		// Update the index to mark the next position as in use by this Read
		index.setNextSeekPosition(byteCount);

		byte[] array = readMetaData.getName().getBytes("UTF8");

		// Write the name
		out.writeInt(array.length);
		out.write(array);

		// Write a single byte for C or U
		out.writeBoolean(readMetaData.isComplemented());

		// Write out its unpadded length (length minus pad characters)
		out.writeInt(readMetaData.getUnpaddedLength());

		// Write out the length of the data
		byte[] data = readMetaData.getRawData();
		out.writeInt(readMetaData.length());
		// And the data itself
		out.write(data);

		// Bytes written:
		//   4   - INT, length of the name to follow
		//   [n] - BYTES, the name itself
		//   1   - BYTE, 0 or 1 (for C or U)
		//   4   - INT, unpadded length
		//   4   - INT, data length
		//   [d] - BYTES, the data
		// = 13
		byteCount += (array.length + 13 + data.length);
	}
}