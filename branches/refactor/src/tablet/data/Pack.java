// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data;

import java.util.*;

public class Pack implements IReadManager, Iterable<PackRow>
{
	protected ArrayList<PackRow> packRows = new ArrayList<PackRow>();

	public Iterator<PackRow> iterator()
		{ return packRows.iterator(); }

	public int size()
		{ return packRows.size(); }

	public void addPackRow(PackRow packRow)
		{ packRows.add(packRow); }

	public void trimToSize()
		{ packRows.trimToSize(); }

	public LineData getLineData(int line, int start, int end)
	{
		PackRow packRow = packRows.get(line);

		return packRow.getLineData(start, end);
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
			Read found = packRow.getReadAt(read.getStartPosition());
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

	public Read[] getPairAtLine(int lineIndex, int colIndex)
	{
		if (lineIndex < 0 || lineIndex >= packRows.size())
			return null;

		PackRow packRow = packRows.get(lineIndex);
		Read readA = packRow.getReadAt(colIndex);
		Read readB = null;
		
		if (readA instanceof MatedRead)
		{
			MatedRead mRead = (MatedRead)readA;
			readB = mRead.getPair();
		}
		return new Read[] { readA, readB };
	}
}