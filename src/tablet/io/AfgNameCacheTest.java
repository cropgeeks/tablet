package tablet.io;

import java.io.*;
import java.util.*;

import junit.framework.*;

import scri.commons.gui.*;

public class AfgNameCacheTest extends TestCase
{
	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.main("tablet.io.AfgNameCacheTest");
	}

	public void testCache()
		throws Exception
	{
		File cacheFile = new File("tablet-cache.dat");

		AfgNameCache cache = new AfgNameCache(cacheFile);
		cache.openForWriting();

		ArrayList<String> rndNames = new ArrayList<String>();
		Random r = new Random();

		for (int i = 0; i < 500; i++)
		{
			String rnd = SystemUtils.createGUID(r.nextInt(96));
			rndNames.add(rnd);
			cache.storeName(rnd);
		}

		cache.openForReading();

		for (int i = 0; i < 500; i++)
		{
			String rnd = cache.getName(i);
			assertEquals(rndNames.get(i), rnd);
		}

		cache.close();
	}
}