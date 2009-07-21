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
}