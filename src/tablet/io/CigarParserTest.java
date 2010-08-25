// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

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

		String read = parser.parse("TTAGATAAAGGATACTG", 7, "8M2I4M1D3M", null);
		System.out.println("TEMP = " + read);
		System.out.println("Expected: TTAGATAAGATA*CTG");
		assertEquals(read, "TTAGATAAGATA*CTG");

		read = parser.parse("AAAAGATAAGGATA", 9, "3S6M1P1I4M", null);
		System.out.println("TEMP = " + read);
		System.out.println("Expected: AGATAAGATA");
		assertEquals(read, "AGATAAGATA");

		read = parser.parse("AGCTAA", 9, "5H6M", null);
		System.out.println("TEMP = " + read);
		System.out.println("Expected: AGCTAA");
		assertEquals(read, "AGCTAA");

		read = parser.parse("ATAGCTTCAGC", 16, "6M14N5M", null);
		System.out.println("TEMP = " + read);
		System.out.println("Expected: ATAGCTNNNNNNNNNNNNNNTCAGC");
		assertEquals(read, "ATAGCTNNNNNNNNNNNNNNTCAGC");
	}
}