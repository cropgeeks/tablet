package av.data;

import java.util.*;

public class DNATable
{
	public static final byte UNKNOWN = 0;
	public static final byte PAD = 1;	// *

	// These are encoded as ATCG (obviously), but also codes for when the
	// same nucleotide (in a read) is NOT (n) the same base as the consensus
	public static final byte A  = 2;
	public static final byte nA = 3;

	public static final byte T  = 4;
	public static final byte nT = 5;

	public static final byte C  = 6;
	public static final byte nC = 7;

	public static final byte G  = 8;
	public static final byte nG = 9;

	public DNATable()
	{

	}

	public static byte getState(char dnaCode)
	{
		switch (dnaCode)
		{
			case 'A': return A;
			case 'a': return A;

			case 'T': return T;
			case 't': return T;

			case 'C': return C;
			case 'c': return C;

			case 'G': return G;
			case 'g': return G;

			case '*': return PAD;

			default: return UNKNOWN;
		}
	}

	public static String getDNA(byte code)
	{
		switch (code)
		{
			case A:  return "A";
			case nA: return "A";
			case T:  return "T";
			case nT: return "T";
			case C:  return "C";
			case nC: return "C";
			case G:  return "G";
			case nG: return "G";

			case PAD: return "*";

			default: return "?";
		}
	}
}