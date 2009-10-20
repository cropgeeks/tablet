// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data;

import java.util.*;

class StackSet implements IReadManager
{
	private ArrayList<Read> stack = new ArrayList<Read>();

	StackSet()
	{
	}

	StackSet(ArrayList<Read> reads)
	{
		stack = reads;
	}

	public int size()
	{
		return stack.size();
	}

	/**
	 * Returns a byte array containing sequence information (or -1 for no data)
	 * for the given line between the points start and end.
	 */
	public byte[] getValues(int line, int start, int end)
	{
		Read read = stack.get(line);

		byte[] data = new byte[end-start+1];

		// Tracking index within the data array
		int dataI = 0;
		// Tracking index on the start->end scale
		int index = start;

		int readS = read.getStartPosition();
		int readE = read.getEndPosition();

		ReadMetaData rmd = Assembly.getReadMetaData(read);

		// Fill in any blanks between the current position and the start of
		// this read
		for (; index <= end && index < readS; index++, dataI++)
			data[dataI] = -1;

		// Fill in any read data
		for (; index <= end && index <= readE; index++, dataI++)
			data[dataI] = rmd.getStateAt(index-readS);

		// If no more reads are within the window, fill in any blanks between
		// the final read and the end of the array
		for (; index <= end; index++, dataI++)
			data[dataI] = -1;

		return data;
	}

	public Read getReadAt(int line, int nucleotidePosition)
	{
		if (line < 0 || line >= stack.size())
			return null;

		Read read = stack.get(line);

		// Check to see if the nucleotide position falls within this read's zone
		if (nucleotidePosition >= read.getStartPosition() &&
			nucleotidePosition <= read.getEndPosition())
		{
			return read;
		}

		return null;
	}
}