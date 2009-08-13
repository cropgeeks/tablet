package tablet.io;

import scri.commons.file.BufferedRandomAccessFile;
import java.io.*;

/**
 * An instance of this class can be used to store a set of read names on disk
 * while parsing an AFG file. This implementation keeps the names in a temporary
 * cache file (before AfgFileReader stores them permanently in the main cache).
 */
class AfgNameCache
{
	private File cacheFile;
	private int entrySize = 75;

	// Used while writing to the cache
	private DataOutputStream out;
	// Used while reading from the cache
	private BufferedRandomAccessFile rnd;

	AfgNameCache(File cacheFile)
	{
		this.cacheFile = cacheFile;
	}

	// Open the cache file for WRITING TO
	void openForWriting()
		throws IOException
	{
		out = new DataOutputStream(new BufferedOutputStream(
			new FileOutputStream(cacheFile)));
	}

	// Open the cache file for READING FROM
	void openForReading()
		throws IOException
	{
		out.close();
		rnd = new BufferedRandomAccessFile(cacheFile, "r", 1024);
		rnd.fillBuffer();
	}

	// Close the cache file
	void close()
		throws Exception
	{
		if (out != null) out.close();
		if (rnd != null) rnd.close();

		cacheFile.delete();
	}

	void storeName(String name)
		throws Exception
	{
		byte[] array = name.getBytes("UTF8");

		out.writeInt(array.length);
		out.write(array);

		// Pad the array to entrySize bytes (-4 bytes for the length of name)
		for (int i = array.length; i < entrySize-4; i++)
			out.writeByte(-1);
	}

	String getName(int index)
		throws Exception
	{
		rnd.seek(index * (entrySize));

		// Read the length (in bytes) that the name takes up
		int length = rnd.readInt();

		// Make an array of this length
		byte[] array = new byte[length];
		// Then read its name from the file
		rnd.read(array);

		return new String(array, "UTF8");
	}
}