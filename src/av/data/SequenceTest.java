package av.data;

import junit.framework.*;

public class SequenceTest extends TestCase
{
	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.main("av.data.SequenceTest");
	}

	public void testSequenceStorage()
		throws Exception
	{
		for (int i = 1; i <= 60; i++)
		{
			Consensus c = new Consensus();

			StringBuffer sb = new StringBuffer(i);
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