package tablet.analysis;

import tablet.data.*;
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
		Consensus c = new Consensus();
		c.setData("*AC*T");
		BaseMappingCalculator bm = new BaseMappingCalculator(c);
		bm.runJob(0);
		int[] array = bm.getPaddedToUnpaddedArray();

		int[] test1 = { -1, 0, 1, -1, 2 };
		for (int i = 0; i < array.length; i++)
			assertEquals(test1[i], array[i]);


		c = new Consensus();
		c.setData("**A**");
		bm = new BaseMappingCalculator(c);
		bm.runJob(0);
		array = bm.getPaddedToUnpaddedArray();

		int[] test2 = { -1, -1, 0, -1, -1 };
		for (int i = 0; i < array.length; i++)
			assertEquals(test2[i], array[i]);


		c = new Consensus();
		c.setData("AC*TG");
		bm = new BaseMappingCalculator(c);
		bm.runJob(0);
		array = bm.getPaddedToUnpaddedArray();

		int[] test3 = { 0, 1, -1, 2, 3 };
		for (int i = 0; i < array.length; i++)
			assertEquals(test3[i], array[i]);


		c = new Consensus();
		c.setData("ACT**");
		bm = new BaseMappingCalculator(c);
		bm.runJob(0);
		array = bm.getPaddedToUnpaddedArray();

		int[] test4 = { 0, 1, 2, -1, -1 };
		for (int i = 0; i < array.length; i++)
			assertEquals(test4[i], array[i]);
	}

	// Tests the mapping between an unpadded value and where it actually lies
	// within the original data array
	public void testCalculatingPaddedPositions()
		throws Exception
	{
		Consensus c = new Consensus();
		c.setData("*AC*T");
		BaseMappingCalculator bm = new BaseMappingCalculator(c);
		bm.runJob(0);
		int[] array = bm.getUnpaddedToPaddedArray();

		int[] test1 = { 1, 2, 4, -1, -1 };
		for (int i = 0; i < array.length; i++)
			assertEquals(test1[i], array[i]);


		c = new Consensus();
		c.setData("*****");
		bm = new BaseMappingCalculator(c);
		bm.runJob(0);
		array = bm.getUnpaddedToPaddedArray();

		int[] test2 = { -1, -1, -1, -1, -1 };
		for (int i = 0; i < array.length; i++)
			assertEquals(test2[i], array[i]);


		c = new Consensus();
		c.setData("AGTCA");
		bm = new BaseMappingCalculator(c);
		bm.runJob(0);
		array = bm.getUnpaddedToPaddedArray();

		int[] test3 = { 0, 1, 2, 3, 4 };
		for (int i = 0; i < array.length; i++)
			assertEquals(test3[i], array[i]);


		c = new Consensus();
		c.setData("A*TCA");
		bm = new BaseMappingCalculator(c);
		bm.runJob(0);
		array = bm.getUnpaddedToPaddedArray();

		int[] test4 = { 0, 2, 3, 4, -1 };
		for (int i = 0; i < array.length; i++)
			assertEquals(test4[i], array[i]);
	}
}
