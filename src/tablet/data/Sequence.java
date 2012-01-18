// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data;

/**
 * Sequence is the base class for objects that need to store DNA
 * sequence information in an efficient mannor.
 */
public class Sequence
{
	// Defines what the pad character will be (can be changed if need be)
	public static String PAD = "*";

	// If true, any DNA ambuguity codes will be read as N rather than unknown
	public static boolean AMBIGUOUS_TO_N = false;

	// The codes that we store for each "state".
	// There are obvious codes for ATCG*N, but also codes for when the base
	// in a read is different (d) from the same base in the consensus.
	// eg, a): consensus=A, and read=A would encode the read as A
	// eg, b): consensus=A, and read=T would encode the read as dT

	// TODO: Fix this...
	// VERY IMPORTANT: The order of these definitions is used elsewhere (WITHOUT
	// CHECKS) so they must be kept this way.
	//   29/06/09: Used by TextColorScheme and StandardColorScheme


	/** Base whose nucleotide is unknown. **/
	public static final byte UNKNOWN = 0;
	/** Base whose nucleotide is unknown, but different to the consensus. */
	public static final byte dUKNOWN = 1;

	/** Base is a pad (*). */
	public static final byte P  = 2;
	/** Base is a pad (*), but different to the consensus. */
	public static final byte dP = 3;
	/** Base is an N. */
	public static final byte N  = 4;
	/** Base is an N, but different to the consensus. */
	public static final byte dN = 5;
	/** Base is an A. */
	public static final byte A  = 6;
	/** Base is an A, but different to the consensus. */
	public static final byte dA = 7;
	/** Base is an C. */
	public static final byte C  = 8;
	/** Base is an C, but different to the consensus. */
	public static final byte dC = 9;
	/** Base is an G. */
	public static final byte G  = 10;
	/** Base is an G, but different to the consensus. */
	public static final byte dG = 11;
	/** Base is an T. */
	public static final byte T  = 12;
	/** Base is an T, but different to the consensus. */
	public static final byte dT = 13;

	public static String[] getStates()
	{
		return new String[] { "?", PAD, "N", "A", "C", "G", "T" };
	}

	public Sequence()
	{
	}

	public Sequence(String sequence)
	{
		setData(new StringBuilder(sequence));
	}

	// Stores the actual DNA states, using one byte for every two states
	private byte[] data;

	public byte[] getRawData()
		{ return data; }

	public void setRawData(byte[] data)
		{ this.data = data; }

	/**
	 * Sets the DNA data for this sequence.
	 * @param sequence the DNA string to store
	 */
	public void setData(StringBuilder sequence)
	{
		int baseCount = sequence.length();

		// Store an even number of bases or an odd number?
		if (baseCount % 2 == 0)
			data = new byte[(baseCount/2)];
		else
			data = new byte[(baseCount/2)+1];

		for (int i = 0, c = 0; i < data.length; i++, c+=2)
		{
			// nibble1 and nibble2
			byte n1 = 0, n2 = 0;

			try	{ n1 = getState(sequence.charAt(c)); }
			catch (StringIndexOutOfBoundsException e) {}

			try { n2 = getState(sequence.charAt(c+1)); }
			catch (StringIndexOutOfBoundsException e) {}

			// Merge the two nibbles into a single byte and store
			data[i] = (byte) ((n1 << 4) | n2);
		}
	}

	/**
	 * Returns the byte code stored at the given index location, where index is
	 * a position from 0 to (length-1) of the sequence.
	 * @param index the index position to return the byte code from
	 * @return the byte code stored at the given index location
	 */
	public byte getStateAt(int index)
	{
		if (index % 2 == 0)
			return (byte) ((data[index/2] >> 4) & 0xF);

		else
			return (byte) (data[index/2] & 0xF);
	}

	/**
	 * Sets the DNA at base position index to be the value of state, where state
	 * is a value listed in the Sequence class's DNA table.
	 * @param index the index position of the base to change
	 * @param state the new value to store at this position
	 */
	public void setStateAt(int index, byte state)
	{
		// TODO: Is there a way to set, eg, n2 without having to read n1 or a
		// way to set n1 without having to read n2?

		// Set n1 (nibble1, lhs)
		if (index % 2 == 0)
		{
			byte n2 = (byte) (data[index/2] & 0xF);
			data[index/2] = (byte) ((state << 4) | n2);
		}

		// Set n2 (nibber2, rhs)
		else
		{
			byte n1 = (byte) ((data[index/2] >> 4) & 0xF);
			data[index/2] = (byte) ((n1 << 4) | state);
		}
	}

	private byte getState(char dnaCode)
	{
		switch (dnaCode)
		{
			case 'A': return A;
			case 'C': return C;
			case 'G': return G;
			case 'T': return T;
			case 'U': return T;
			case 'N': return N;
			case 'X': return N;

			// TODO: Any other potential pad characters in use in file types?
			// "*" is used in ACE files, "-" is used often elsewhere
			case '*': return P;
			case '-': return P;

			case 'a': return A;
			case 'c': return C;
			case 'g': return G;
			case 't': return T;
			case 'u': return T;
			case 'n': return N;
			case 'x': return N;
		}

		if (AMBIGUOUS_TO_N)
		{
			switch (dnaCode)
			{
				case 'K': return N;
				case 'M': return N;
				case 'R': return N;
				case 'Y': return N;
				case 'S': return N;
				case 'W': return N;
				case 'B': return N;
				case 'V': return N;
				case 'H': return N;
				case 'D': return N;
			}
		}

		return UNKNOWN;
	}

	public static String getDNA(byte state)
	{
		switch (state)
		{
			case A:  return "A";
			case C:  return "C";
			case G:  return "G";
			case T:  return "T";

			case N:  return "N";
			case P:  return PAD;

			case dP: return PAD;
			case dA: return "A";
			case dC: return "C";
			case dG: return "G";
			case dT: return "T";
			case dN: return "N";

			default: return "?";
		}
	}

	public static String getComplementaryDNA(byte state)
	{
		switch (state)
		{
			case A:  return "T";
			case C:  return "G";
			case G:  return "C";
			case T:  return "A";

			case N:  return "N";
			case P:  return PAD;

			case dP: return PAD;
			case dT: return "A";
			case dG: return "C";
			case dC: return "G";
			case dA: return "T";
			case dN: return "N";

			default: return "?";
		}
	}

	public int length()
	{
		throw new UnsupportedOperationException();
	}
}