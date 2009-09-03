// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data;

import junit.framework.*;

public class SequenceTest extends TestCase
{
	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.main("tablet.data.SequenceTest");
	}

	public void testSequenceStorage()
		throws Exception
	{
		for (int i = 1; i <= 10; i++)
		{
			Consensus c = new Consensus();

			StringBuilder sb = new StringBuilder(i);
			for (int s = 0; s < i; s++)
				sb.append(rndNucleotide());

			String str1 = sb.toString();
			System.out.println(str1);
			c.setData(str1);

			String str2 = c.toString();
			System.out.println(str2);

			assertEquals(str1, str2);
		}
	}

	public void testSettingChanges()
	{
		System.out.println();

		Consensus c = new Consensus();

		StringBuilder sb = new StringBuilder(10);
		for (int s = 0; s < 10; s++)
			sb.append(rndNucleotide());

		String str1 = sb.toString();
		System.out.println(str1);
		c.setData(str1);

		for (int i = 0; i < c.length(); i++)
		{
			byte b = c.getStateAt(i);
			c.setStateAt(i, b);
		}

		String str2 = c.toString();
		System.out.println(str2);

		assertEquals(str1, str2);
	}

	private String rndNucleotide()
	{
		switch ((int) (Math.random() * 5))
		{
			case 0: return "A";
			case 1: return "C";
			case 2: return "G";
			case 3: return "T";
			case 4: return "*";
		}

		return "?";
	}
}