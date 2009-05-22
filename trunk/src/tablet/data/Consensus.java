package tablet.data;

/** The consensus sequence for a contig. */
public class Consensus extends Sequence
{
	private byte[] bq;

	/** Constructs a new, empty consensus sequence. */
	public Consensus()
	{
	}

	/**
	 * Sets the base score qualities for this consensus. This parses a string
	 * that is expected to contains a space separated list of scores, with NO
	 * SCORES provided for bases that are padded.
	 * TODO: This should really be a part of the AceFileReader class
	 */
	public void setBaseQualities(String qualities)
		throws Exception
	{
		bq = new byte[length()];

		String[] tokens = qualities.trim().split("\\s+");

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

	/**
	 * Returns an array of base quality data, starting at start and ending at
	 * end. These values may be outside of the actual start and end values for
	 * the consensus, in which case -1 will be returned for those positions.
	 * @param start the starting index
	 * @param end the ending index (inclusive)
	 * @return an array of base quality data
	 */
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
	 * Returns an array of sequence data, starting at start and ending at end.
	 * These values may be outside of the actual start and end values for the
	 * consensus, in which case -1 will be returned for those positions.
	 * @param start the starting index
	 * @param end the ending index (inclusive)
	 * @return an array of sequence data
	 */
	public byte[] getRange(int start, int end)
	{
		byte[] data = new byte[end-start+1];

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