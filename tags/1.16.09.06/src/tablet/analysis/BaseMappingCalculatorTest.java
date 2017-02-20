// Copyright 2009-2016 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.analysis;

import tablet.data.*;
import tablet.data.cache.*;
import tablet.data.auxiliary.*;

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
		Consensus c = new Consensus("*AC*T");
		IArrayIntCache paddedToUnpadded = new ArrayIntMemCache(5);
		IArrayIntCache unpaddedToPadded = new ArrayIntMemCache(5);
		BaseMappingCalculator bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();
		MappingData mappingData = new MappingData(c);
		mappingData.setPaddedToUnpaddedCache(bm.getPaddedToUnpadded());
		mappingData.setUnpaddedToPaddedCache(bm.getUnpaddedToPadded());

		int[] test1 = { -1, 0, 1, -1, 2 };
		for (int i = 0; i < test1.length; i++)
			assertEquals(test1[i], mappingData.getPaddedToUnpadded(i));


		c = new Consensus("**A**");
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();
		mappingData = new MappingData(c);
		mappingData.setPaddedToUnpaddedCache(bm.getPaddedToUnpadded());
		mappingData.setUnpaddedToPaddedCache(bm.getUnpaddedToPadded());

		int[] test2 = { -1, -1, 0, -1, -1 };
		for (int i = 0; i < test2.length; i++)
			assertEquals(test2[i], mappingData.getPaddedToUnpadded(i));


		c = new Consensus("AC*TG");
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();
		mappingData = new MappingData(c);
		mappingData.setPaddedToUnpaddedCache(bm.getPaddedToUnpadded());
		mappingData.setUnpaddedToPaddedCache(bm.getUnpaddedToPadded());

		int[] test3 = { 0, 1, -1, 2, 3 };
		for (int i = 0; i < test3.length; i++)
			assertEquals(test3[i], mappingData.getPaddedToUnpadded(i));


		c = new Consensus("ACT**");
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();
		mappingData = new MappingData(c);
		mappingData.setPaddedToUnpaddedCache(bm.getPaddedToUnpadded());
		mappingData.setUnpaddedToPaddedCache(bm.getUnpaddedToPadded());

		int[] test4 = { 0, 1, 2, -1, -1 };
		for (int i = 0; i < test4.length; i++)
			assertEquals(test4[i], mappingData.getPaddedToUnpadded(i));

		c = new Consensus("***ACGTA****CGTCA***");
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();
		mappingData = new MappingData(c);
		mappingData.setPaddedToUnpaddedCache(bm.getPaddedToUnpadded());
		mappingData.setUnpaddedToPaddedCache(bm.getUnpaddedToPadded());

		int[] test5 = { -1, -1, -1, 0, 1, 2, 3, 4, -1, -1, -1, -1, 5, 6, 7, 8, 9, -1, -1, -1 };
		for (int i = 0; i < test5.length; i++)
			assertEquals(test5[i], mappingData.getPaddedToUnpadded(i));

		c = new Consensus("*GTACGTAC*AGCGTCACA*");
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();
		mappingData = new MappingData(c);
		mappingData.setPaddedToUnpaddedCache(bm.getPaddedToUnpadded());
		mappingData.setUnpaddedToPaddedCache(bm.getUnpaddedToPadded());

		int[] test6 = { -1, 0, 1, 2, 3, 4, 5, 6, 7, -1, 8, 9, 10, 11, 12, 13, 14, 15, 16, -1 };
		for (int i = 0; i < test6.length; i++)
			assertEquals(test6[i], mappingData.getPaddedToUnpadded(i));

		c = new Consensus("*G*A*G*A*C*G*G*C*C*T");
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();
		mappingData = new MappingData(c);
		mappingData.setPaddedToUnpaddedCache(bm.getPaddedToUnpadded());
		mappingData.setUnpaddedToPaddedCache(bm.getUnpaddedToPadded());

		int[] test7 = { -1, 0, -1, 1, -1, 2, -1, 3, -1, 4, -1, 5, -1, 6, -1, 7, -1, 8, -1, 9 };
		for (int i = 0; i < test7.length; i++)
			assertEquals(test7[i], mappingData.getPaddedToUnpadded(i));

		c = new Consensus("TGGACGTAGCAGTGTCACGT");
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();
		mappingData = new MappingData(c);
		mappingData.setPaddedToUnpaddedCache(bm.getPaddedToUnpadded());
		mappingData.setUnpaddedToPaddedCache(bm.getUnpaddedToPadded());

		int[] test8 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 };
		for (int i = 0; i < test8.length; i++)
			assertEquals(test8[i], mappingData.getPaddedToUnpadded(i));

		c = new Consensus("********************");
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();
		mappingData = new MappingData(c);
		mappingData.setPaddedToUnpaddedCache(bm.getPaddedToUnpadded());
		mappingData.setUnpaddedToPaddedCache(bm.getUnpaddedToPadded());

		int[] test9 = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
		for (int i = 0; i < test9.length; i++)
			assertEquals(test9[i], mappingData.getPaddedToUnpadded(i));

		c = new Consensus("AGCT**G*TA**CTA**TG");
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();
		mappingData = new MappingData(c);
		mappingData.setPaddedToUnpaddedCache(bm.getPaddedToUnpadded());
		mappingData.setUnpaddedToPaddedCache(bm.getUnpaddedToPadded());

		int[] test10 = { 0, 1, 2, 3, -1, -1, 4, -1, 5, 6, -1, -1, 7, 8, 9, -1, -1, 10, 11 };
		for (int i = 0; i < test10.length; i++)
			assertEquals(test10[i], mappingData.getPaddedToUnpadded(i));
	}

	// Tests the mapping between an unpadded value and where it actually lies
	// within the original data array
	public void testCalculatingPaddedPositions()
		throws Exception
	{
		Consensus c = new Consensus("*AC*T");
		IArrayIntCache paddedToUnpadded = new ArrayIntMemCache(5);
		IArrayIntCache unpaddedToPadded = new ArrayIntMemCache(5);
		BaseMappingCalculator bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();
		MappingData mappingData = new MappingData(c);
		mappingData.setPaddedToUnpaddedCache(bm.getPaddedToUnpadded());
		mappingData.setUnpaddedToPaddedCache(bm.getUnpaddedToPadded());

		int[] test1 = { 1, 2, 4, -1, -1 };
		for (int i = 0; i < test1.length; i++)
		{
			int result = mappingData.getUnpaddedToPadded(i);
			if(result >= c.length())
				result = -1;
			assertEquals(test1[i], result);
		}


		c = new Consensus("*****");
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();
		mappingData = new MappingData(c);
		mappingData.setPaddedToUnpaddedCache(bm.getPaddedToUnpadded());
		mappingData.setUnpaddedToPaddedCache(bm.getUnpaddedToPadded());

		int[] test2 = { -1, -1, -1, -1, -1 };
		for (int i = 0; i < test2.length; i++)
		{
			int result = mappingData.getUnpaddedToPadded(i);
			if(result >= c.length())
				result = -1;
			assertEquals(test2[i], result);
		}


		c = new Consensus("AGTCA");
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();
		mappingData = new MappingData(c);
		mappingData.setPaddedToUnpaddedCache(bm.getPaddedToUnpadded());
		mappingData.setUnpaddedToPaddedCache(bm.getUnpaddedToPadded());

		int[] test3 = { 0, 1, 2, 3, 4 };
		for (int i = 0; i < test3.length; i++)
		{
			int result = mappingData.getUnpaddedToPadded(i);
			if(result >= c.length())
				result = -1;
			assertEquals(test3[i], result);
		}


		c = new Consensus("A*TCA");
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();
		mappingData = new MappingData(c);
		mappingData.setPaddedToUnpaddedCache(bm.getPaddedToUnpadded());
		mappingData.setUnpaddedToPaddedCache(bm.getUnpaddedToPadded());

		int[] test4 = { 0, 2, 3, 4, -1 };
		for (int i = 0; i < test4.length; i++)
		{
			int result = mappingData.getUnpaddedToPadded(i);
			if(result >= c.length())
				result = -1;
			assertEquals(test4[i], result);
		}

		c = new Consensus("***ACGTA****CGTCA***");
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();
		mappingData = new MappingData(c);
		mappingData.setPaddedToUnpaddedCache(bm.getPaddedToUnpadded());
		mappingData.setUnpaddedToPaddedCache(bm.getUnpaddedToPadded());

		int[] test5 = { 3, 4, 5, 6, 7, 12, 13, 14, 15, 16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
		for (int i = 0; i < test5.length; i++)
		{
			int result = mappingData.getUnpaddedToPadded(i);
			if(result >= c.length())
				result = -1;
			assertEquals(test5[i], result);
		}

		c = new Consensus("*GTACGTAC*AGCGTCACA*");
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();
		mappingData = new MappingData(c);
		mappingData.setPaddedToUnpaddedCache(bm.getPaddedToUnpadded());
		mappingData.setUnpaddedToPaddedCache(bm.getUnpaddedToPadded());

		int[] test6 = { 1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 17, 18, -1};
		for (int i = 0; i < test6.length; i++)
		{
			int result = mappingData.getUnpaddedToPadded(i);
			if(result >= c.length())
				result = -1;
			assertEquals(test6[i], result);
		}

		c = new Consensus("*G*A*G*A*C*G*G*C*C*T");
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();
		mappingData = new MappingData(c);
		mappingData.setPaddedToUnpaddedCache(bm.getPaddedToUnpadded());
		mappingData.setUnpaddedToPaddedCache(bm.getUnpaddedToPadded());

		int[] test7 = { 1, 3, 5, 7, 9, 11, 13, 15, 17, 19, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
		for (int i = 0; i < test7.length; i++)
		{
			int result = mappingData.getUnpaddedToPadded(i);
			if(result >= c.length())
				result = -1;
			assertEquals(test7[i], result);
		}

		c = new Consensus("TGGACGTAGCAGTGTCACGT");
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();
		mappingData = new MappingData(c);
		mappingData.setPaddedToUnpaddedCache(bm.getPaddedToUnpadded());
		mappingData.setUnpaddedToPaddedCache(bm.getUnpaddedToPadded());

		int[] test8 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 };
		for (int i = 0; i < test8.length; i++)
		{
			int result = mappingData.getUnpaddedToPadded(i);
			if(result >= c.length())
				result = -1;
			assertEquals(test8[i], result);
		}

		c = new Consensus("********************");
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();
		mappingData = new MappingData(c);
		mappingData.setPaddedToUnpaddedCache(bm.getPaddedToUnpadded());
		mappingData.setUnpaddedToPaddedCache(bm.getUnpaddedToPadded());

		int[] test9 = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
		for (int i = 0; i < test9.length; i++)
		{
			int result = mappingData.getUnpaddedToPadded(i);
			if(result >= c.length())
				result = -1;
			assertEquals(test9[i], result);
		}

		c = new Consensus("AGCT**G*TA**CTA**TG");
		paddedToUnpadded = new ArrayIntMemCache(5);
		unpaddedToPadded = new ArrayIntMemCache(5);
		bm = new BaseMappingCalculator(c, paddedToUnpadded, unpaddedToPadded);
		bm.run();
		mappingData = new MappingData(c);
		mappingData.setPaddedToUnpaddedCache(bm.getPaddedToUnpadded());
		mappingData.setUnpaddedToPaddedCache(bm.getUnpaddedToPadded());

		int[] test10 = { 0, 1, 2, 3, 6, 8, 9, 12, 13, 14, 17, 18, -1, -1, -1, -1, -1, -1, -1  };
		for (int i = 0; i < test10.length; i++)
		{
			int result = mappingData.getUnpaddedToPadded(i);
			if(result >= c.length())
				result = -1;
			assertEquals(test10[i], result);
		}
	}
}