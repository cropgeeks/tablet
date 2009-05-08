package tablet.data;

public class Consensus extends Sequence
{
	private byte[] bq;

	public Consensus()
	{
	}

	public void setBaseQualities(String qualities)
		throws Exception
	{
		bq = new byte[length()];

		String[] tokens = qualities.trim().split(" ");

		int i = 0;
		for (int t = 0; t < tokens.length; t++, i++)
		{
			// Skip padded bases, because the quality string doesn't score them
			while (getStateAt(i) == Sequence.P)
			{
				bq[i] = -1;
				i++;
			}

			bq[i] = Byte.parseByte(tokens[t]);
		}
	}

	public byte[] getBaseQualityRange(int start, int end)
	{
		byte[] data = new byte[end-start+1];

		int i = 0, d = 0;
		int length = bq.length;

		// Pre sequence data
		for (i = start; i < 0 && i <= end; i++, d++)
			data[d] = -1;

		// Sequence data
		for (i = i; i <= end && i < length; i++, d++)
			data[d] = bq[i];

		// Post sequence data
		for (i = i; i <= end; i++, d++)
			data[d] = -1;

		return data;
	}

	/**
	 * Returns an array of data for this sequence starting at start and ending
	 * at end, but including any indices that may be outside of this sequence's
	 * data, eg, less than 0 and greater than length()
	 */
	public byte[] getRange(int start, int end)
	{
		byte[] data = new byte[end-start+1];
		System.out.println("length is " + data.length);

		int i = 0, d = 0;
		int length = length();

		// Pre sequence data
		for (i = start; i < 0 && i <= end; i++, d++)
			data[d] = -1;

		// Sequence data
		for (i = i; i <= end && i < length; i++, d++)
			data[d] = getStateAt(i);

		// Post sequence data
		for (i = i; i <= end; i++, d++)
			data[d] = -1;

		return data;
	}

	void print()
	{
		System.out.println();
		System.out.println("Consensus:");
		System.out.println("  length: " + length());

		for (int i = 0; i < length(); i++)
		{
//			System.out.print(DNATable.getDNA(data[i]));
			System.out.print(bq[i] + " ");
		}

		System.out.println();
	}
}