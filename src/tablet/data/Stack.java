// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data;

import java.util.*;

public class Stack implements IReadManager
{
	private ArrayList<Read> stack = new ArrayList<Read>();

	Stack()
	{
	}

	Stack(ArrayList<Read> reads)
	{
		stack = reads;
	}

	public int size()
	{
		return stack.size();
	}

	public LineData getLineData(int line, int start, int end)
	{
		Read read = stack.get(line);

		ReadMetaData[] reads = new ReadMetaData[end-start+1];
		int[] indexes = new int[end-start+1];

		// Tracking index within the data array
		int dataI = 0;
		// Tracking index on the start->end scale
		int index = start;

		int readS = read.getStartPosition();
		int readE = read.getEndPosition();

		ReadMetaData rmd = Assembly.getReadMetaData(read, true);

		// Fill in any blanks between the current position and the start of
		// this read
		for (; index <= end && index < readS; index++, dataI++)
			indexes[dataI] = -1;

		// Fill in any read data
		for (; index <= end && index <= readE; index++, dataI++)
		{
			reads[dataI] = rmd;
			indexes[dataI] = index-readS;
		}

		// If no more reads are within the window, fill in any blanks between
		// the final read and the end of the array
		for (; index <= end; index++, dataI++)
			indexes[dataI] = -1;

		return new LineData(indexes, reads);
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

	/**
	 * Method which uses a binary search to find the correct lineIndex for
	 * a given read.
	 *
	 * @param read	The read whose lineIndex we are searching for.
	 * @return mid	The lineIndex.
	 */
	public int getLineForRead(Read read)
	{
		int high = stack.size()-1;
		int low = 0;

		while(high >= low)
		{
			int mid = low + ((high-low) /2);

			if (stack.get(mid).compareTo(read) == -1)
				low = mid+1;
			else if(stack.get(mid).compareTo(read) == 1)
				high = mid-1;
			else
			{
				//we've potentially found the read, but must check to ensure
				//we have as reads can be equal without being the desired read.
				if(stack.get(mid).getID() == read.getID())
					return mid;
				else
				{
					while(stack.get(mid).getStartPosition() >= read.getStartPosition())
					{
						mid--;
						if(stack.get(mid).getID() == read.getID())
							return mid;
					}
					while(stack.get(mid).getStartPosition() <= read.getStartPosition())
					{
						mid++;
						if(stack.get(mid).getID() == read.getID())
							return mid;
					}
				}
			}
		}
		return -1;
	}

	public ArrayList<Read> getReadNames(int startIndex, int endIndex)
	{
		ArrayList<Read> tempList = new ArrayList<Read>();
		for(int i=startIndex; i <=endIndex; i++)
		{
			tempList.add(stack.get(i));
		}

		return tempList;
	}

	public ArrayList<Read> getLine(int line)
	{
		Read read = stack.get(line);
		ArrayList<Read> stackLine = new ArrayList<Read>();
		stackLine.add(read);
		return stackLine;
	}

	/**
	 * Return the pair of reads that can be found at the given line (and near
	 * the given column) in the display.
	 */
	public Read[] getPairAtLine(int lineIndex, int colIndex)
	{
		if (lineIndex < 0 || lineIndex >= stack.size())
			return null;
		
		Read readA = stack.get(lineIndex);
		Read readB = null;

		if (readA instanceof MatedRead)
		{
			MatedRead mRead = (MatedRead) readA;
			readB = mRead.getPair();
		}

		return new Read[] { readA, readB };
	}
}