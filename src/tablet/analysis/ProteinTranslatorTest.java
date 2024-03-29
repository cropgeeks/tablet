// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.analysis;

import tablet.data.*;

import junit.framework.*;

public class ProteinTranslatorTest extends TestCase
{
	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.main("tablet.analysis.ProteinTranslatorTest");
	}

	public void testTranslatingForwardFrame1()
		throws Exception
	{
		// Translations verified with:
		// bioinformatics.picr.man.ac.uk/research/software/tools/sequenceconverter.html

		String[] dna = new String[] {
			"ATGAGGAGGTGGAAGAGGAAGCTGGGCCGCACTCTCACCCGCTTCCTCTCCAAGCCCCCTTTCAAGCCCAAGCCCACCAA",
			"CCCCTCGCCGCCGCCGCCCCCGCCGCCGCCGGGGATCCAGCCTCCTCCACCGGCGCTGCCCGGCATGCCGCACGGACGCC",
			"CGCCGCCGCCGTTCCCGGGAGGGCGGGACGCGTTCCCGCAGGCGGCGTCGACGGTGGTCCCGGACCCGGCCAGGTTCTTC"
		};

		String[] protein = new String[] {
			"MRRWKRKLGRTLTRFLSKPPFKPKPT",
			"PLAAAAPAAAGDPASSTGAARHAART",
			"RRRRSREGGTRSRRRRRRWSRTRPGS"
		};

		for (int i = 0; i < dna.length; i++)
		{
			Consensus c = null;

			c = new Consensus(dna[i]);

			ProteinTranslator pt = new ProteinTranslator(
				0, c.getSequence(), c.length(), ProteinTranslator.Direction.FORWARD, 1);
			pt.enableUnitTest();
			pt.run();

			String translation = pt.getTranslationAsString();
			assertEquals(protein[i], translation);
		}
	}

	public void testTranslatingForwardFrame2Gapped()
		throws Exception
	{
		// Translations verified with:
		// bioinformatics.picr.man.ac.uk/research/software/tools/sequenceconverter.html

		String[] dna = new String[] {
			"CCTATCCCCTGTGTGCCTTGCCT****ACTGTTGCGTGTCTCAGCGGCCT",
			"TGTTGTCTTTCG*CCCAC*TCACATGCTAGGTTCTTGGCC**AAC**TGA",
			"GATTGCAGTGAGGAAATGCA*GAAAAAAA**TGAGAACTGAAGCAACATT"
		};

		String[] protein = new String[] {
			"LSPVCLAYCCVSQRP",
			"VVFRPLTC.VLGQL",
			"IAVRKCRKK.ELKQH"
		};

		for (int i = 0; i < dna.length; i++)
		{
			Consensus c = null;

			c = new Consensus(dna[i]);

			ProteinTranslator pt = new ProteinTranslator(
				0, c.getSequence(), c.length(), ProteinTranslator.Direction.FORWARD, 2);
			pt.enableUnitTest();
			pt.run();

			String translation = pt.getTranslationAsString();
			assertEquals(protein[i], translation);
		}
	}

	public void testTranslatingReverseFrame1Gapped()
		throws Exception
	{
		// Translations tested against:
		// bioinformatics.picr.man.ac.uk/research/software/tools/sequenceconverter.html

		String[] dna = new String[] {
			"CCTATCCCCTGTGTGCCTTGCCT****ACTGTTGCGTGTCTCAGCGGCCT",
			"TGTTGTCTTTCG*CCCAC*TCACATGCTAGGTTCTTGGCC**AAC**TGA",
			"GATTGCAGTGAGGAAATGCA*GAAAAAAA**TGAGAACTGAAGCAACATT"
		};

		String[] protein = new String[] {
			"RPLRHATVGKAHRG.",
			"SVGQEPSM.VGERQ",
			"NVASVLIFFCISSLQ"
		};

		for (int i = 0; i < dna.length; i++)
		{
			Consensus c = null;

			c = new Consensus(dna[i]);

			ProteinTranslator pt = new ProteinTranslator(
				0, c.getSequence(), c.length(), ProteinTranslator.Direction.REVERSE, 1);
			pt.enableUnitTest();
			pt.run();

			String translation = pt.getTranslationAsString();
			assertEquals(protein[i], translation);
		}
	}
}