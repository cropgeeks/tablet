// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data.cache;

import java.io.*;

import junit.framework.*;

import tablet.data.*;

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

			cache.setReadMetaData(new ReadMetaData(name, i % 2 == 0, i));
		}

		cache.close();

		cache = cache.createReadableCache(cacheFile, indexFile);

		for (int i = 0; i < 10; i++)
		{
			ReadMetaData rmd = cache.getReadMetaData(i);

			String expected = "NAME_" + i;
			String fromFile = rmd.getName();
			assertEquals(expected, fromFile);

			boolean isComplemented = (i % 2 == 0);
			boolean isFromFile     = rmd.isComplemented();
			assertEquals(isComplemented, isFromFile);

			System.out.println(fromFile + ", " + isFromFile);
		}
	}
}