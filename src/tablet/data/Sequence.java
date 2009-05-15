package tablet.data;

public abstract class Sequence
{
	// The codes that we store for each "state".
	// There are obvious codes for ATCG*N, but also codes for when the base
	// in a read is different (d) from the same base in the consensus.
	// eg, a): consensus=A, and read=A would encode the read as A
	// eg, b): consensus=A, and read=T would encode the read as dT

	public static final byte NOTUSED = 0;

	public static final byte UNKNOWN = 1;
	public static final byte dUKNOWN = 2;

	public static final byte P  = 3;	// P = pad (*)
	public static final byte dP = 4;
	public static final byte A  = 5;
	public static final byte dA = 6;
	public static final byte T  = 7;
	public static final byte dT = 8;
	public static final byte C  = 9;
	public static final byte dC = 10;
	public static final byte G  = 11;
	public static final byte dG = 12;
	public static final byte N  = 13;
	public static final byte dN = 14;

	// Stores the actual DNA states, using one byte for every two states
	private byte[] data;

	/**
	 * Returns the length of this sequence.
	 * @return length the length of this sequence
	 */
	public int length()
	{
//		byte n1 = (byte) ((data[data.length-1] >> 4) & 0xF);
		byte n2 = (byte) (data[data.length-1] & 0xF);

		// If storing an odd number of bases
		if (n2 == NOTUSED)
			return (2 * data.length) -1;

		// Else if storing an even number of bases
		else
			return (2 * data.length);
	}

	/**
	 * Sets this sequence object to be the same as the sequence string passed
	 * in, using the DNA table to perform the appropriate dna->byte translation
	 * required for optimum storage in memory.
	 */
	public void setData(String sequence)
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
	 */
	public byte getStateAt(int index)
	{
		if (index % 2 == 0)
			return (byte) ((data[index/2] >> 4) & 0xF);

		else
			return (byte) (data[index/2] & 0xF);
	}

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
			case 'A': return A;  case 'a': return A;
			case 'T': return T;  case 't': return T;
			case 'C': return C;  case 'c': return C;
			case 'G': return G;  case 'g': return G;
			case 'N': return N;  case 'n': return N;
			case '*': return P;

			default: return UNKNOWN;
		}
	}

	private String getDNA(byte state)
	{
		switch (state)
		{
			case A:  return "A";  case dA: return "A";
			case T:  return "T";  case dT: return "T";
			case C:  return "C";  case dC: return "C";
			case G:  return "G";  case dG: return "G";
			case N:  return "N";  case dN: return "N";
			case P: return "*";   case dP: return "*";

			default: return "?";
		}
	}

	void print()
	{
		for (int i = 0, c = 0; i < data.length; i++, c+=2)
		{
			byte n1 = (byte) ((data[i] >> 4) & 0xF);
			byte n2 = (byte) (data[i] & 0xF);

			System.out.print(getDNA(n1));
			if (n2 != NOTUSED)
				System.out.print(getDNA(n2));
		}
	}

	public String toString()
	{
		int length = length();

		StringBuffer sb = new StringBuffer(length);

		for (int i = 0; i < length; i++)
		{
			byte state = getStateAt(i);
			sb.append(getDNA(state));
		}

		return sb.toString();
	}

	public byte[] getSequence()
	{
		byte[] array = new byte[length()];

		for (int i = 0; i < array.length; i++)
			array[i] = getStateAt(i);

		return array;
	}
}