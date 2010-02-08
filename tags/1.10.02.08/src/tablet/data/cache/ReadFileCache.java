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
	private ArrayLongFileCache index;

	// When writing, how many bytes have been written to the cache?
	private long byteCount = 0;

	public ReadFileCache(File cacheFile, File indexFile)
	{
		this.cacheFile = cacheFile;
		index = new ArrayLongFileCache(indexFile);
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

					byte[] data;
					if(dataLength % 2 == 0)
						data = new byte[dataLength/2];
					else
						data = new byte[(dataLength/2)+1];
					rnd.read(data);

					if (dataOnly)
					{
						rmd = new ReadMetaData();
						rmd.setRawData(data);
					}
					else
					{
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

						rmd = new ReadMetaData(name, isComplemented, unpaddedLength);
						rmd.setRawData(data);
						rmd.setLength(dataLength);

						// Read CIGAR data (but only with BAM assemblies)
						if (Assembly.hasCigar())
						{
							byte[] cigar = new byte[rnd.readIntFromBuffer()];

							rnd.read(cigar);
							rmd.setCigar(new String(cigar, "UTF8"));
						}
					}
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

		byte[] array = readMetaData.getName().getBytes("UTF8");

		// Write the name
		out.writeInt(array.length);
		out.write(array);

		// Write a single byte for C or U
		out.writeBoolean(readMetaData.isComplemented());

		// Write out its unpadded length (length minus pad characters)
		out.writeInt(readMetaData.getUnpaddedLength());

		byte[] cigar = readMetaData.getCigar().getBytes("UTF8");

		// Write the name
		out.writeInt(cigar.length);
		out.write(cigar);

		// Bytes written:
		//   4   - INT, length of the name to follow
		//   [n] - BYTES, the name itself
		//   1   - BYTE, 0 or 1 (for C or U)
		//   4   - INT, unpadded length
		//   4   - INT, data length
		//   [d] - BYTES, the data
		//	 4	 - INT, length of the cigar string
		//	 [c] - BYTES, the cigar itself
		// = 17
		byteCount += (array.length + 17 + data.length + cigar.length);
	}
}