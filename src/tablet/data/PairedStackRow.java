// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data;

/**
 * Stores information about one row in the PairedStack display. Includes a
 * simple array of reads, with space for two reads.
 */
public class PairedStackRow
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

	public LineData getPixelData(int startBase, int arraySize, float scale, boolean getMetaData)
	{
		// Arrays which will eventually make up the LineData object
		ReadMetaData[] rmds = new ReadMetaData[arraySize];
		int[] indexes = new int[arraySize];
		Read[] pixReads = new Read[arraySize];

		ReadMetaData rmdA = null;
		ReadMetaData rmdB = null;

		// If we're rendering data at the current zoom level, grab the RMDs for
		// the reads on this row of the Stack
		if (scale >= 1 || getMetaData)
		{
			rmdA = Assembly.getReadMetaData(readA, true);
			if (readB != null)
				rmdB = Assembly.getReadMetaData(readB, true);
		}

		for (int i=0; i < arraySize; i++)
		{
			int base = startBase + (int)(i / scale);

			// If base is found within the first read
			if (base >= readA.s() && base <= readA.e())
			{
				indexes[i] = base - readA.s();
				rmds[i] = rmdA;
				pixReads[i] = readA;
			}
			// If base is found within the second read
			else if (readB != null && base >= readB.s() && base <= readB.e())
			{
				indexes[i] = base - readB.s();
				rmds[i] = rmdB;
				pixReads[i] = readB;
			}
			// If base is part of a pair link line
			else if (base > readA.e() && readB != null && base < readB.s())
				indexes[i] = -2;
			// Base isn't over any data
			else
				indexes[i] = -1;
		}

		return new LineData(indexes, rmds, pixReads);
	}

	/**
	 * Returns the read at the given position, otherwise returns null
	 */
	Read getReadAt(int position)
	{
		if (readA.s() <= position && readA.e() >= position)
			return readA;

		else if (readB != null && readB.s() <= position && readB.e() >= position)
			return readB;

		return null;
	}

	Read[] getBothReads()
	{
		return new Read[] {readA, readB};
	}
}