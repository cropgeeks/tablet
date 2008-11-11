package av.data;

public abstract class Sequence
{
	protected byte[] name;
	protected byte[] data;


	public String getName()
		{ return new String(name); }


	public String toString()
		{ return new String(name); }

	/**
	 * Returns the length of this sequence.
	 * @return length the length of this sequence
	 */
	public int length()
		{ return data.length; }

	/**
	 * Sets this sequence object to be the same as the sequence string passed
	 * in, using the DNA table to perform the appropriate dna->byte translation
	 * required for optimum storage in memory.
	 */
	public void setData(String sequence)
	{
		data = new byte[sequence.length()];

		for (int c = 0; c < data.length; c++)
			data[c] = DNATable.getState(sequence.charAt(c));
	}
}