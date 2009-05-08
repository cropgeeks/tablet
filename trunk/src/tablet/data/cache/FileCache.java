package tablet.data.cache;

import java.io.*;

import tablet.data.*;

/**
 * Concrete implementation of the IDataCache interface that stores its data in
 * a binary file on disk. Each element written to the file is always written
 * using the same number of bytes, allowing for random read-back from any
 * location with a fixed access time.
 */
public class FileCache implements IReadCache
{
	// TODO: FileCache: This class needs to be made to work properly with
	// unicode character streams

	private CacheIndex index = new CacheIndex();

	// Used while writing to the cache
	private BufferedOutputStream out;
	// Used while reading from the cache
	private RandomAccessFile rnd;

	// When writing, how many bytes have been written to the cache?
	private long byteCount = 0;
	// When writing, how many names have been stored in the cache?
	private int count = 0;

	private FileCache()
	{
	}

	public static FileCache createWritableCache(File cacheFile, File indexFile)
		throws IOException
	{
		FileCache fc = new FileCache();

		fc.out = new BufferedOutputStream(new FileOutputStream(cacheFile));
		fc.index.createWritableIndex(indexFile);

		return fc;
	}

	public static FileCache createReadableCache(File cacheFile, File indexFile)
		throws IOException
	{
		FileCache fc = new FileCache();
		fc.rnd = new RandomAccessFile(cacheFile, "r");
		fc.index.createReadableIndex(indexFile);

		return fc;
	}

	public void close()
		throws IOException
	{
		System.out.println("Closing cache: " + count + ": " + byteCount);

		if (out != null)
			out.close();
		if (rnd != null)
			rnd.close();

		index.close();
	}

	public ReadMetaData getReadMetaData(int id)
	{
		try
		{
			long seekTo = index.getSeekPosition(id);
			rnd.seek(seekTo);

			// Read the length (in bytes) that the name takes up
			int length = rnd.read();

			// Make an array of this length
			byte[] array = new byte[length];
			// Then read its name from the file
			rnd.read(array);
			String name = new String(array, "UTF8");

			// Then C or U
			boolean isComplemented = rnd.readBoolean();

			return new ReadMetaData(name, isComplemented);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public int setReadMetaData(ReadMetaData readMetaData)
		throws Exception
	{
		// Update the index to mark the next position as in use by this Read
		index.setNextSeekPosition(byteCount);

		byte[] array = readMetaData.getName().getBytes("UTF8");

		// Write the name
		out.write(array.length);
		out.write(array);

		// Write a single byte for C or U
		out.write((byte) (readMetaData.isComplemented() ? 1 : 0));

		// Bytes written:
		//  1   - BYTE, length of the name to follow
		//  [n] - BYTES, the name itself
		//  1   - BYTE, 0 or 1 (for C or U)
		byteCount += (1 + array.length + 1);

		return count++;
	}
}