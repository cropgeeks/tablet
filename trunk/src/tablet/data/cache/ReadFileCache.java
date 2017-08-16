// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

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

					byte numberInPair = rnd.readByte();

					short readGroup = rnd.readShortFromBuffer();

					byte b = rnd.readByte();

					// No left shift required as desired byte is least significant
					// bit Bitwise & check returns 1 when both operands are 1 (in
					// this case it means it won't evaluate to 0 if the bit in the
					// byte is set)
					boolean isComplemented = ((b & 1) != 0);
					boolean isPaired = ((b & (1 << 1)) != 0);
					boolean mateMapped = ((b & (1 << 2)) != 0);
					boolean isMateContig = ((b & (1 << 3)) != 0);

					rmd = new ReadMetaData(isComplemented);
					rmd.setRawData(data);
					rmd.setLength(dataLength);
					rmd.setNumberInPair(numberInPair);
					rmd.setIsPaired(isPaired);
					rmd.setMateMapped(mateMapped);
					rmd.setReadGroup(readGroup);
					rmd.setIsMateContig(isMateContig);
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

		// Byte (0, 1, or 2) for pair information
		out.writeByte(readMetaData.getNumberInPair());

		// Writre a short for the readGroup of the read
		out.writeShort(readMetaData.getReadGroup());

		byte b = packByte(readMetaData);

		out.write(b);

		// Bytes written:
		//   4   - INT, data length
		//   [d] - BYTES, the data
		//   1   - BYTE, number in pair
		//	 1	 - BYTE, is paired, mate mapped, complemented, isMateContig
		//	 2	 - SHORT, read group
		// = 10
		byteCount += (8 + data.length);
	}

	/**
	 * Sets bits in a byte at specific positions representing the four booleans
	 * in the ReadMetaData class. The bit at position 0 is set with 0 or 1 for
	 * complemented. Similarly the bits at positions 1, 2 and 3 are set for
	 * isPaired, mateMapped and isMateContig. Bits set using bitwise or operator
	 * which sets the bit to 1 if either operand is 1.
	 */
	private byte packByte(ReadMetaData rmd)
	{
		byte b = 0;

		// No bit shift required bit we want is lsb.
		if (rmd.isComplemented())
			b = (byte) (b | 1);

		// Need to shift each of the following by the given number of positions
		// so 1 becomes 10, 1 becomes 100 and 1 becomes 1000
		if (rmd.getIsPaired())
			b = (byte) (b | (1 << 1));

		if (rmd.getMateMapped())
			b = (byte) (b | (1 << 2));

		if (rmd.isMateContig())
			b = (byte) (b | (1 << 3));

		return b;
	}
}