// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data;

import java.util.*;

public class Pack implements IReadManager, Iterable<PackRow>
{
	protected ArrayList<PackRow> packRows = new ArrayList<>();

	public Iterator<PackRow> iterator()
		{ return packRows.iterator(); }

	public int size()
		{ return packRows.size(); }

	public void addPackRow(PackRow packRow)
		{ packRows.add(packRow); }

	public void trimToSize()
		{ packRows.trimToSize(); }

	public LineData getPixelData(int line, int start, int end, float scale, boolean getMetaData)
	{
		PackRow packRow = packRows.get(line);

		return packRow.getPixelData(start, end, scale, getMetaData);
	}

	public Read getReadAt(int line, int nucleotidePosition)
	{
		if (line < 0 || line >= packRows.size())
			return null;

		PackRow packRow = packRows.get(line);

		return packRow.getReadAt(nucleotidePosition);
	}

	public int getLineForRead(Read read)
	{
		for(PackRow packRow : packRows)
		{
			Read found = packRow.getReadAt(read.s());
			if (found != null && found.getID() == read.getID())
				return packRows.indexOf(packRow);
		}
		return -1;
	}

	public PackRow get(int i)
	{
		return packRows.get(i);
	}

	public ArrayList<Read> getReadNames(int startIndex, int endIndex)
	{
		return null;
	}

	public ArrayList<Read> getLine(int line)
	{
		return packRows.get(line).getReads();
	}

	public Read[] getPairForLink(int rowIndex, int colIndex)
		{ return null; }
}