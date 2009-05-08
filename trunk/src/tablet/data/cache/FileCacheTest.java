package tablet.data.cache;

import java.io.*;

import junit.framework.*;

public class FileCacheTest extends TestCase
{
	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.main("tablet.data.cache.FileCacheTest");
	}

	public void testFileCache()
		throws Exception
	{
		File cacheFile = new File("tablet-cache.dat");
		File indexFile = new File("tablet-index.dat");

		FileCache cache = FileCache.createWritableCache(cacheFile, indexFile);

		for (int i = 0; i < 10; i++)
		{
			String name = "NAME_" + i;

			cache.setName(name);
		}

		cache.close();

		cache = cache.createReadableCache(cacheFile, indexFile);

		for (int i = 0; i < 5; i++)
		{
			String expected = "NAME_" + i;
			String fromFile = cache.getName(i);

			System.out.println("Expected: " + expected + ", read: " + fromFile);

			assertEquals(expected, fromFile);
		}
	}
}