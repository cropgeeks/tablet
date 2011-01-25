// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis;

import java.util.*;

import tablet.analysis.tasks.*;
import tablet.data.*;
import static tablet.data.Sequence.*;

public class ProteinTranslator extends BackgroundTask
{
	public static enum Direction { FORWARD, REVERSE };

	public static int LBASE = 22;
	public static int RBASE = 44;

	public HashMap<String, Integer> acids;
	public String[] codes;

	private Sequence sequence;
	private static String STOP = ".";

	private int index;
	private Direction direction;
	private int readingFrame;
	private short[] protein;

	// Builds up a DNA string as we go along, eg, AC**T will eventually fill the
	// array with A, C, and T (reading forward), or T, C, A (in reverse)
	private	String[] seq;
	// The indices of the three nucleotides that make up the current codon
	private int[] dna;

	// Only needed for the unit test (stores a human readable translation)
	private StringBuilder translation;

	public ProteinTranslator()
	{
		createTranslationTable();
	}

	/**
	 * Creates a new ProteinTranslator, ready to translate a sequence with the
	 * specified Direction and reading frame.
	 */
	public ProteinTranslator(
		int index, Sequence sequence, Direction direction, int readingFrame)
	{
		this();

		this.index = index;
		this.sequence = sequence;
		this.direction = direction;
		this.readingFrame = readingFrame - 1;
	}

	void enableUnitTest()
	{
		int length = sequence.length();
		translation = new StringBuilder(length);
	}

	public int getIndex()
		{ return index; }

	public static void setStopCharacter(String stopChar)
	{
		STOP = stopChar;
	}

	public void run()
	{
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		Thread.currentThread().setName("ProteinTranslator:" + index);

		try
		{
			int length = sequence.length();

			protein = new short[length];

			if (direction == Direction.FORWARD)
				translateForward();
			else
				translateReverse();
		}
		catch (Throwable e)
		{
			System.out.println("ProteinTranslator: " + index + ": " + e);
			okToRun = false;
		}

		notifyAndFinish();
	}

	protected void doCleanup()
	{
		protein = null;
	}

	private void translateForward()
		throws Exception
	{
		seq = new String[3];
		dna = new int[3];
		int s = 0;

		int length = sequence.length();
		int startAt = 0;

		// Find the first "real" base (skips translating NNNNN etc at start)
		for (startAt = 0; startAt < length && okToRun; startAt++)
			if (sequence.getStateAt(startAt) >= A)
				break;

		for (int i = startAt + readingFrame; i < length && okToRun; i++)
		{
			byte state = sequence.getStateAt(i);

			if (state >= N)
			{
				dna[s] = i;
				seq[s] = Sequence.getDNA(state);

				s++;
			}

			// Once we have 3 nucleotides, do the translation
			if (s == 3)
			{
				s = 0;

				Integer code = acids.get(seq[0] + seq[1] + seq[2]);

				if (code == null) // null returned when we get codons with N
					code = 22;

				// Assign the protein to the first nucleotide of the three
				protein[dna[0]] = (short) ((int)code + LBASE);
				// Assign the protein (colour info only) to the other two
				protein[dna[1]] = (short) (int)code;
				protein[dna[2]] = (short) ((int)code + RBASE);

				if (translation != null)
					translation.append(codes[code]);
			}
		}
	}

	// Reading in reverse means using the complementary version of the actual
	// DNA we've gathered, so if ACG is found, it is converted to TGC
	private void translateReverse()
		throws Exception
	{
		seq = new String[3];
		dna = new int[3];
		int s = 0;

		int length = sequence.length();
		int startAt = length - 1;

		// Find the first "real" base (skips translating NNNNN etc at start)
		for (; startAt >= 0 && okToRun; startAt--)
			if (sequence.getStateAt(startAt) >= A)
				break;

		for (int i = startAt-readingFrame; i >= 0 && okToRun; i--)
		{
			byte state = sequence.getStateAt(i);

			if (state >= N)
			{
				dna[s] = i;
				seq[s] = Sequence.getComplementaryDNA(state);

				s++;
			}

			// Once we have 3 nucleotides, do the translation
			if (s == 3)
			{
				s = 0;

				Integer code = acids.get(seq[0] + seq[1] + seq[2]);

				if (code == null)
					code = 22;

				// Assign the protein to the first nucleotide of the three
				protein[dna[2]] = (short) ((int)code + LBASE);
				// Assign the protein (colour info only) to the other two
				protein[dna[1]] = (short) (int)code;
				protein[dna[0]] = (short) (int)(code + RBASE);

				if (translation != null)
					translation.append(codes[code]);
			}
		}
	}

	public short[] getTranslation()
		{ return protein; }

	String getTranslationAsString()
		{ return translation.toString(); }

	private void createTranslationTable()
	{
		acids = new HashMap<String, Integer>();
		codes = new String[23];

		codes[0] = "?";

		// 1 = I
		codes[1] = "I";
		acids.put("ATT", 1);
		acids.put("ATC", 1);
		acids.put("ATA", 1);

		// 2 = L
		codes[2] = "L";
		acids.put("CTT", 2);
		acids.put("CTC", 2);
		acids.put("CTA", 2);
		acids.put("CTG", 2);
		acids.put("TTA", 2);
		acids.put("TTG", 2);

		// 3 = V
		codes[3] = "V";
		acids.put("GTT", 3);
		acids.put("GTC", 3);
		acids.put("GTA", 3);
		acids.put("GTG", 3);

		// 4 = F
		codes[4] = "F";
		acids.put("TTT", 4);
		acids.put("TTC", 4);

		// 5 = M
		codes[5] = "M";
		acids.put("ATG", 5);

		// 6 = C
		codes[6] = "C";
		acids.put("TGT", 6);
		acids.put("TGC", 6);

		// 7 = A
		codes[7] = "A";
		acids.put("GCT", 7);
		acids.put("GCC", 7);
		acids.put("GCA", 7);
		acids.put("GCG", 7);

		// 8 = G
		codes[8] = "G";
		acids.put("GGT", 8);
		acids.put("GGC", 8);
		acids.put("GGA", 8);
		acids.put("GGG", 8);

		// 9 = P
		codes[9] = "P";
		acids.put("CCT", 9);
		acids.put("CCC", 9);
		acids.put("CCA", 9);
		acids.put("CCG", 9);

		// 10 = T
		codes[10] = "T";
		acids.put("ACT", 10);
		acids.put("ACC", 10);
		acids.put("ACA", 10);
		acids.put("ACG", 10);

		// 11 = S
		codes[11] = "S";
		acids.put("TCT", 11);
		acids.put("TCC", 11);
		acids.put("TCA", 11);
		acids.put("TCG", 11);
		acids.put("AGT", 11);
		acids.put("AGC", 11);

		// 12 = Y
		codes[12] = "Y";
		acids.put("TAT", 12);
		acids.put("TAC", 12);

		// 13 = W
		codes[13] = "W";
		acids.put("TGG", 13);

		// 14 = Q
		codes[14] = "Q";
		acids.put("CAA", 14);
		acids.put("CAG", 14);

		// 15 = N
		codes[15] = "N";
		acids.put("AAT", 15);
		acids.put("AAC", 15);

		// 16 = H
		codes[16] = "H";
		acids.put("CAT", 16);
		acids.put("CAC", 16);

		// 17 = E
		codes[17] = "E";
		acids.put("GAA", 17);
		acids.put("GAG", 17);

		// 18 = D
		codes[18] = "D";
		acids.put("GAT", 18);
		acids.put("GAC", 18);

		// 19 = K
		codes[19] = "K";
		acids.put("AAA", 19);
		acids.put("AAG", 19);

		// 20 = R
		codes[20] = "R";
		acids.put("CGT", 20);
		acids.put("CGC", 20);
		acids.put("CGA", 20);
		acids.put("CGG", 20);
		acids.put("AGA", 20);
		acids.put("AGG", 20);

		// 21 = .
		codes[21] = STOP;
		acids.put("TAA", 21);
		acids.put("TAG", 21);
		acids.put("TGA", 21);

		// 22 = X
		codes[22] = "X";
	}
}