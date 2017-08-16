// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data;

import java.util.*;

/**
 * Holds the list of PairedStack elements and is used for access to the reads
 * when utilising the Paired StackSet viewing mode.
 */
public class PairedStack implements IReadManager
{
	private ArrayList<PairedStackRow> stack = new ArrayList<>();

	@Override
	public LineData getPixelData(int line, int start, int end, float scale, boolean getMetaData)
	{
		PairedStackRow pairedStackRow = stack.get(line);

		return pairedStackRow.getPixelData(start, end, scale, getMetaData);
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

		PairedStackRow pairedStackRow = stack.get(line);

		return pairedStackRow.getReadAt(nucleotidePosition);
	}

	/**
	 * Get the line of the display the given read can be found on.
	 */
	@Override
	public int getLineForRead(Read read)
	{
		for(PairedStackRow pairedStackRow : stack)
		{
			Read found = pairedStackRow.getReadAt(read.s());

			if (found != null && found.getID() == read.getID())
				return stack.indexOf(pairedStackRow);
		}
		return -1;
	}

	@Override
	public ArrayList<Read> getReadNames(int startIndex, int endIndex)
	{
		return null;
	}

	public void addPairedStackRow(PairedStackRow pairedStackRow)
	{
		stack.add(pairedStackRow);
	}

	@Override
	public ArrayList<Read> getLine(int line)
	{
		ArrayList<Read> reads = new ArrayList<>();
		for (Read read : stack.get(line).getBothReads())
		{
			if(read != null)
				reads.add(read);
		}
		return reads;
	}

	@Override
	public Read[] getPairForLink(int rowIndex, int colIndex)
	{
		if (rowIndex >= 0 && rowIndex < stack.size())
		{
			// Get the pair of reads for this line
			Read[] pair = stack.get(rowIndex).getBothReads();

			// But only return them if it really is a pair (ie TWO reads), and
			// the point asked for is between them (on the link)
			if (pair[1] != null && colIndex > pair[0].e() && colIndex < pair[1].s())
				return pair;
		}

		return null;
	}
}