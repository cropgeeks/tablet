// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis;

import tablet.data.*;
import tablet.data.cache.*;

import junit.framework.*;

public class BaseMappingCalculatorTest extends TestCase
{
	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.main("tablet.analysis.BaseMappingCalculatorTest");
	}

	public void testCalculatingUnpaddedPositions()
		throws Exception
	{
		Consensus c = new Consensus();
		c.setData(new StringBuilder("*AC*T"));
		IArrayIntCache paddedToUnpadded = new ArrayIntMemCache(5);
		IArrayIntCache unpaddedToPadded = new ArrayIntMemCache(5);
		BaseMappingCalculator bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();

		int[] test1 = { -1, 0, 1, -1, 2 };
		for (int i = 0; i < paddedToUnpadded.length(); i++)
			assertEquals(test1[i], paddedToUnpadded.getValue(i));


		c = new Consensus();
		c.setData(new StringBuilder("**A**"));
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();

		int[] test2 = { -1, -1, 0, -1, -1 };
		for (int i = 0; i < paddedToUnpadded.length(); i++)
			assertEquals(test2[i], paddedToUnpadded.getValue(i));


		c = new Consensus();
		c.setData(new StringBuilder("AC*TG"));
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();

		int[] test3 = { 0, 1, -1, 2, 3 };
		for (int i = 0; i < paddedToUnpadded.length(); i++)
			assertEquals(test3[i], paddedToUnpadded.getValue(i));


		c = new Consensus();
		c.setData(new StringBuilder("ACT**"));
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();

		int[] test4 = { 0, 1, 2, -1, -1 };
		for (int i = 0; i < paddedToUnpadded.length(); i++)
			assertEquals(test4[i], paddedToUnpadded.getValue(i));
	}

	// Tests the mapping between an unpadded value and where it actually lies
	// within the original data array
	public void testCalculatingPaddedPositions()
		throws Exception
	{
		Consensus c = new Consensus();
		c.setData(new StringBuilder("*AC*T"));
		IArrayIntCache paddedToUnpadded = new ArrayIntMemCache(5);
		IArrayIntCache unpaddedToPadded = new ArrayIntMemCache(5);
		BaseMappingCalculator bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();

		int[] test1 = { 1, 2, 4, -1, -1 };
		for (int i = 0; i < unpaddedToPadded.length(); i++)
			assertEquals(test1[i], unpaddedToPadded.getValue(i));


		c = new Consensus();
		c.setData(new StringBuilder("*****"));
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();

		int[] test2 = { -1, -1, -1, -1, -1 };
		for (int i = 0; i < unpaddedToPadded.length(); i++)
			assertEquals(test2[i], unpaddedToPadded.getValue(i));


		c = new Consensus();
		c.setData(new StringBuilder("AGTCA"));
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();

		int[] test3 = { 0, 1, 2, 3, 4 };
		for (int i = 0; i < unpaddedToPadded.length(); i++)
			assertEquals(test3[i], unpaddedToPadded.getValue(i));


		c = new Consensus();
		c.setData(new StringBuilder("A*TCA"));
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();

		int[] test4 = { 0, 2, 3, 4, -1 };
		for (int i = 0; i < unpaddedToPadded.length(); i++)
			assertEquals(test4[i], unpaddedToPadded.getValue(i));
	}
}