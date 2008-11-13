package av.data.cache;

import java.io.*;

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

	// The number of "units" per string written to the file (might be bytes or
	// chars depending on the implementation)
	private int nUnits = 50;

	// Used while writing to the cache
	private BufferedOutputStream out;
	// Used while reading from the cache
	private RandomAccessFile rnd;

	// When writing, how many names have been stored in the cache?
	private int count = 0;

	private FileCache()
	{
	}

	public static FileCache createWritableCache(File cacheFile)
		throws IOException
	{
		FileCache fc = new FileCache();
		fc.out = new BufferedOutputStream(new FileOutputStream(cacheFile));

		return fc;
	}

	public static FileCache createReadableCache(File cacheFile)
		throws IOException
	{
		FileCache fc = new FileCache();
		fc.rnd = new RandomAccessFile(cacheFile, "r");

		return fc;
	}

	public void close()
		throws IOException
	{
		if (out != null)
			out.close();
		if (rnd != null)
			rnd.close();
	}

	public String getName(int id)
	{
		long seekTo = id * nUnits;

		try
		{
			byte[] array = new byte[nUnits];

			rnd.seek(seekTo);
			rnd.read(array);

			return new String(array, "UTF8").trim();
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public int setName(String name)
		throws Exception
	{
		byte[] array = name.getBytes();

		for (int i = 0; i < nUnits; i++)
		{
			if (i < array.length)
				out.write(array[i]);
			else
				out.write(' ');
		}

		return count++;
	}
}