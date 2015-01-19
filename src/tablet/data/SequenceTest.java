// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

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
			StringBuilder sb = new StringBuilder(i);
			for (int s = 0; s < i; s++)
				sb.append(rndNucleotide());

			System.out.println(sb);
			Consensus c = new Consensus(sb.toString());

			String str2 = c.toString();
			System.out.println(str2);

			assertEquals(sb.toString(), str2);
		}
	}

	public void testSettingChanges()
		throws Exception
	{
		System.out.println();

		StringBuilder sb = new StringBuilder(10);
		for (int s = 0; s < 10; s++)
			sb.append(rndNucleotide());

		System.out.println(sb);
		Consensus c = new Consensus(sb.toString());

		for (int i = 0; i < c.length(); i++)
		{
			byte b = c.getSequence().getStateAt(i);
			c.getSequence().setStateAt(i, b);
		}

		String str2 = c.toString();
		System.out.println(str2);

		assertEquals(sb.toString(), str2);
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