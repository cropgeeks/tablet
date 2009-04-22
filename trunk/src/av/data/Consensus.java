package av.data;

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

		System.out.println("bq.length:  " + bq.length);
		System.out.println("scan count: " + i);
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