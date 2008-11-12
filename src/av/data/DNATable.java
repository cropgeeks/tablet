package av.data;

import java.util.*;

public class DNATable
{
	public static final byte UNKNOWN = 0;

	// These are encoded as ATCG (obviously), but also codes for when the base
	// in a read is different (d) from the same base in the consensus

	public static final byte PAD = 1;	// *
	public static final byte dPAD = 2;

	public static final byte A  = 3;
	public static final byte dA = 4;

	public static final byte T  = 5;
	public static final byte dT = 6;

	public static final byte C  = 7;
	public static final byte dC = 8;

	public static final byte G  = 9;
	public static final byte dG = 10;

	public static final byte N = 11;
	public static final byte dN = 12;

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

			case 'N': return N;
			case 'n': return N;

			case '*': return PAD;

			default: return UNKNOWN;
		}
	}

	public static String getDNA(byte code)
	{
		switch (code)
		{
			case A:  return "A";
			case dA: return "A";
			case T:  return "T";
			case dT: return "T";
			case C:  return "C";
			case dC: return "C";
			case G:  return "G";
			case dG: return "G";
			case N:  return "N";
			case dN: return "N";

			case PAD: return "*";
			case dPAD: return "*";

			default: return "?";
		}
	}
}