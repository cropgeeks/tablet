// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data.cache;

import java.io.*;

import junit.framework.*;

import tablet.data.*;

public class ReadFileCacheTest extends TestCase
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

		ReadFileCache cache = new ReadFileCache(cacheFile, indexFile);
		cache.openForWriting();

		for (int i = 0; i < 10; i++)
		{
			String name = "NAME_" + i;

			ReadMetaData rmd = new ReadMetaData(name, i % 2 == 0, i);
			rmd.setData(new StringBuilder("GATTACA"));

			cache.setReadMetaData(rmd);
		}

		cache.openForReading();

		for (int i = 0; i < 10; i++)
		{
			ReadMetaData rmd = cache.getReadMetaData(i, false);

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