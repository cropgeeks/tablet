// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.data;

import java.util.*;

public class PackSet implements Iterable<Pack>, IReadManager
{
	private ArrayList<Pack> packs = new ArrayList<Pack>();

	public Iterator<Pack> iterator()
		{ return packs.iterator(); }

	public int size()
		{ return packs.size(); }

	public void addPack(Pack pack)
		{ packs.add(pack); }

	public void trimToSize()
		{ packs.trimToSize(); }

	/**
	 * Returns a byte array containing sequence information (or -1 for no data)
	 * for the given line between the points start and end.
	 */
	public byte[] getValues(int line, int start, int end)
	{
		Pack pack = packs.get(line);

		return pack.getValues(start, end);
	}

	public Read getReadAt(int line, int nucleotidePosition)
	{
		if (line < 0 || line >= packs.size())
			return null;

		Pack pack = packs.get(line);

		return pack.getReadAt(nucleotidePosition);
	}

	public int getLineForRead(Read read)
	{
		for(Pack pack : packs)
		{
			Read found = pack.getReadAt(read.getStartPosition());
			if(found != null && found.getID() == read.getID())
				return packs.indexOf(pack);
			else
				continue;
		}
		return -1;
	}

	public Pack get(int i)
	{
		return packs.get(i);
	}

	public ArrayList<Read> getReadNames(int startIndex, int endIndex)
	{
		return null;
	}

	/**
	 * Return the pair of reads that can be found at the given line (and near
	 * the given column) in the display.
	 */
	public Read[] getPairAtLine(int lineIndex, int colIndex)
	{
		if (lineIndex < 0 || lineIndex >= packs.size())
			return null;

		return packs.get(lineIndex).getPair(colIndex);
	}
}