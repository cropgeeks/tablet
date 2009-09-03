// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

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

			c = new Consensus();
			c.setData(dna[i]);

			ProteinTranslator pt = new ProteinTranslator(
				c, ProteinTranslator.Direction.FORWARD, 1);
			pt.runJob(0);

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

			c = new Consensus();
			c.setData(dna[i]);

			ProteinTranslator pt = new ProteinTranslator(
				c, ProteinTranslator.Direction.FORWARD, 2);
			pt.runJob(0);

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

			c = new Consensus();
			c.setData(dna[i]);

			ProteinTranslator pt = new ProteinTranslator(
				c, ProteinTranslator.Direction.REVERSE, 1);
			pt.runJob(0);

			String translation = pt.getTranslationAsString();
			assertEquals(protein[i], translation);
		}
	}
}