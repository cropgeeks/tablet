package tablet.io;

import junit.framework.*;

public class CigarParserTest extends TestCase
{
	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.main("tablet.io.CigarParserTest");
	}

	public void testCigar()
		throws Exception
	{
		CigarParser parser = new CigarParser();

		String temp = parser.cigarDecoder("TTAGATAAAGGATACTG", 7, "8M2I4M1D3M");
		System.out.println("TEMP = " + temp);
		System.out.println("Expected: TTAGATAAAGGATA*CTG");
		assertEquals(temp, "TTAGATAAAGGATA*CTG");
		assertEquals(parser.getPosition(), 7);

		temp = parser.cigarDecoder("AAAAGATAAGGATA", 9, "3S6M1P1I4M");
		System.out.println("TEMP = " + temp);
		System.out.println("Expected: AAAAGATAA*GGATA");
		assertEquals(temp, "AAAAGATAA*GGATA");
		assertEquals(parser.getPosition(), 6);

		temp = parser.cigarDecoder("AGCTAA", 9, "5H6M");
		System.out.println("TEMP = " + temp);
		System.out.println("Expected: AGCTAA");
		assertEquals(temp, "AGCTAA");
		assertEquals(parser.getPosition(), 9);

		temp = parser.cigarDecoder("ATAGCTTCAGC", 16, "6M14N5M");
		System.out.println("TEMP = " + temp);
		System.out.println("Expected: ATAGCTNNNNNNNNNNNNNNTCAGC");
		assertEquals(temp, "ATAGCTNNNNNNNNNNNNNNTCAGC");
		assertEquals(parser.getPosition(), 16);
	}
}
