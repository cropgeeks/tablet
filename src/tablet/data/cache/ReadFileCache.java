// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data.cache;

import java.io.*;

import tablet.data.*;


/**
 * Concrete implementation of the IDataCache interface that stores its data in
 * a binary file on disk. The number of bytes written per element is stored in
 * an accompanying index file (see FileCacheIndex), allowing for random
 * read-back from any location with a fixed access time.
 */
public class ReadFileCache extends TabletCache implements IReadCache
{
	private File indexFile;
	private ArrayLongFileCache index;

	// When writing, how many bytes have been written to the cache?
	private long byteCount = 0;

	public ReadFileCache(File cacheFile, File indexFile)
	{
		this.cacheFile = cacheFile;
		this.indexFile = indexFile;

		cacheFile.deleteOnExit();
		indexFile.deleteOnExit();

		index = new ArrayLongFileCache(indexFile);
	}

	public ReadFileCache resetCache()
		throws IOException
	{
		close();

		return new ReadFileCache(cacheFile, indexFile);
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

	public ReadMetaData getReadMetaData(int id, boolean dataOnly)
	{
		while (true)
		{
			try
			{
				ReadMetaData rmd = null;

				fileLock.acquire();

				try
				{
					long seekTo = index.getValue(id);
					rnd.seek(seekTo);

					int dataLength = rnd.readIntFromBuffer();

					// Data first
					byte[] data;
					if(dataLength % 2 == 0)
						data = new byte[dataLength/2];
					else
						data = new byte[(dataLength/2)+1];
					rnd.read(data);

					// Then C or U
					boolean isComplemented = rnd.readBooleanFromBuffer();

					int numberInPair = rnd.read();

					boolean isPaired = rnd.readBooleanFromBuffer();

					boolean mateMapped = rnd.readBooleanFromBuffer();

					short readGroup = rnd.readShort();

					rmd = new ReadMetaData(isComplemented);
					rmd.setRawData(data);
					rmd.setLength(dataLength);
					rmd.setNumberInPair(numberInPair);
					rmd.setIsPaired(isPaired);
					rmd.setMateMapped(mateMapped);
					rmd.setReadGroup(readGroup);
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
		index.addValue(byteCount);

		// Write out the length of the data
		byte[] data = readMetaData.getRawData();
		out.writeInt(readMetaData.length());
		// And the data itself
		out.write(data);

		// Write a single byte for C or U
		out.writeBoolean(readMetaData.isComplemented());

		// Byte (0, 1, or 2) for pair information
		out.writeByte(readMetaData.getNumberInPair());

		// Write a boolean for whether or not this is a paired read
		out.writeBoolean(readMetaData.getIsPaired());

		// Write a boolean for whether or not its mate is mapped
		out.writeBoolean(readMetaData.getMateMapped());

		// Writre a short for the readGroup of the read
		out.writeShort(readMetaData.getReadGroup());

		// Bytes written:
		//   4   - INT, data length
		//   [d] - BYTES, the data
		//   1   - BYTE, 0 or 1 (for C or U)
		//   1   - BYTE, number in pair
		//	 1	 - BOOLEAN, is paired
		//	 1	 - BOOLEAN, mate mapped
		//	 2	 - SHORT, read group
		// = 10
		byteCount += (10 + data.length);
	}
}