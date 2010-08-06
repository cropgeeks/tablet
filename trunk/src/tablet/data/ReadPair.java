package tablet.data;

/**
 * Stores information about one row in the PairedStack display. Includes a
 * simple array of reads, with space for two reads.
 */
public class ReadPair
{
	private Read readA = null;
	private Read readB = null;

	public void addRead(Read read)
	{
		if(readA == null)
			readA = read;
		else
			readB = read;
	}

	/**
	 * Creates and fills an array with all reads that fit within the given
	 * window start and end positions.
	 */
	public byte[] getValues(int start, int end, int scheme)
	{
		byte[] data = new byte[end-start+1];

		// Tracking index within the data array
		int dataI = 0;
		// Tracking index on the start->end scale
		int index = start;

		Read [] pair = new Read [] {readA, readB};

		// loop over the reads in the data window
		for(int i=0; i < pair.length; i++)
		{
			Read read = pair[i];

			if(read == null || read.getStartPosition() > end)
				break;

			int readS = read.getStartPosition();
			int readE = read.getEndPosition();

			ReadMetaData rmd = Assembly.getReadMetaData(read, true);

			if(read instanceof MatedRead)
			{
				MatedRead matedRead = (MatedRead)read;
				for (; index < readS; index++, dataI++)
				{
					// Is this an area between the start of each read in a pair of reads
					if(matedRead.getMatePos() < start && matedRead.getMatePos() < readS && matedRead.isMateContig())
					{
						data[dataI] = 14;
						// Is this an outlier which has the reads in the pair on different rows.
						if(matedRead.getPair() != null && matedRead.getPair().getEndPosition() > matedRead.getStartPosition())
							data[dataI] = -1;
					}
					else
						data[dataI] = -1;
				}
			}
			else
			{
				for (; index < readS; index++, dataI++)
				{
					data[dataI] = -1;
				}
			}

			// Determine color offset
			int color = rmd.getColorSchemeAdjustment(scheme);

			// Fill in the read data
			for (; index <= end && index <= readE; index++, dataI++)
				data[dataI] = (byte) (color + rmd.getStateAt(index-readS));
		}

		Read read = pair[0];

		if(read instanceof MatedRead)
		{
			MatedRead matedRead = (MatedRead)read;
			// If no more reads are within the window, fill in any blanks between
			// the final read and the end of the array
			for (; index <= end; index++, dataI++)
			{
				if(index > matedRead.getEndPosition() && matedRead.getMatePos() > end || (matedRead.getMatePos() < start && matedRead.getMatePos() < matedRead.getStartPosition() && index < matedRead.getStartPosition() && matedRead.isMateContig()))
					data[dataI] = 14;
				else
					data[dataI] = -1;

//				if(matedRead.getPair() != null && matedRead.getPair().getEndPosition() > matedRead.getStartPosition())
//					data[dataI] = -1;
			}
		}
		else
		{
			for (; index <= end; index++, dataI++)
				data[dataI] = -1;
		}

		return data;
	}

	/**
	 * Returns the read at the given position, otherwise returns null
	 */
	Read getReadAt(int position)
	{
		if(readA.getStartPosition() <= position && readA.getEndPosition() >= position)
			return readA;

		else if(readB != null && readB != null && readB.getStartPosition() <= position && readB.getEndPosition() >= position)
			return readB;

		return null;
	}

	public Read [] getPair(int colIndex)
	{
		if(readB == null)
		{
			if(readA instanceof MatedRead)
			{
				MatedRead matedRead = (MatedRead) readA;
				if (matedRead.getPair() != null)
					return new Read [] {readA, matedRead.getPair()};
			}
		}

		return new Read [] {readA, readB};
	}
}
