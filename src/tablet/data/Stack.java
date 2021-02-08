// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data;

import java.util.*;

public class Stack implements IReadManager
{
	private ArrayList<Read> stack = new ArrayList<>();

	Stack()
	{
	}

	@Override
	public LineData getPixelData(int line, int startBase, int arraySize, float scale, boolean getMetaData)
	{
		// Arrays which will eventually make up the LineData object
		ReadMetaData[] rmds = new ReadMetaData[arraySize];
		int[] indexes = new int[arraySize];
		Read[] pixReads = new Read[arraySize];

		// The read on the given line of the display
		Read read = stack.get(line);
		ReadMetaData rmd = null;

		// If we're rendering data at the current zoom level, grab the RMD for
		// the read on this row of the Stack
		if (scale >= 1 || getMetaData)
			rmd = Assembly.getReadMetaData(read, true);

		for (int i=0, readS = read.s(), readE = read.e(); i < arraySize; i++)
		{
			int base = startBase + (int)(i / scale);

			if (base >= readS && base <= readE)
			{
				rmds[i] = rmd;
				// Index (within the read) of its data at this base
				indexes[i] = base-readS;
				pixReads[i] = read;
			}
			else
				indexes[i] = -1;
		}

		return new LineData(indexes, rmds, pixReads);
	}

	Stack(ArrayList<Read> reads)
	{
		stack = reads;
	}

	@Override
	public int size()
	{
		return stack.size();
	}

	@Override
	public Read getReadAt(int line, int nucleotidePosition)
	{
		if (line < 0 || line >= stack.size())
			return null;

		Read read = stack.get(line);

		// Check to see if the nucleotide position falls within this read's zone
		if (nucleotidePosition >= read.s() &&
			nucleotidePosition <= read.e())
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
	@Override
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
					while(stack.get(mid).s() >= read.s())
					{
						mid--;
						if(stack.get(mid).getID() == read.getID())
							return mid;
					}
					while(stack.get(mid).s() <= read.s())
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

	@Override
	public ArrayList<Read> getReadNames(int startIndex, int endIndex)
	{
		ArrayList<Read> tempList = new ArrayList<>();
		for(int i=startIndex; i <=endIndex; i++)
		{
			tempList.add(stack.get(i));
		}

		return tempList;
	}

	@Override
	public ArrayList<Read> getLine(int line)
	{
		Read read = stack.get(line);
		ArrayList<Read> stackLine = new ArrayList<>();
		stackLine.add(read);
		return stackLine;
	}

	@Override
	public Read[] getPairForLink(int rowIndex, int colIndex)
		{ return null; }
}