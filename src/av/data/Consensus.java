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
		bq = new byte[data.length];
		String[] tokens = qualities.split(" ");

		for (int t = 0, i = 0; t < tokens.length; t++, i++)
		{
			// Skip padded bases, because the quality string doesn't score them
			while (data[i] == DNATable.PAD)
			{
				bq[i] = -1;
				i++;
			}

			bq[i] = Byte.parseByte(tokens[t]);
		}
	}

	public void print()
	{
		for (int i = 0; i < data.length; i++)
		{
			System.out.print(DNATable.getDNA(data[i]));
			System.out.println("\t" + bq[i]);
		}
	}
}