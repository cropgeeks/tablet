package av.data;

import java.util.*;

class PackSet implements Iterable<Pack>, IReadManager
{
	private Vector<Pack> packs = new Vector<Pack>();

	PackSet()
	{
	}

	PackSet(Vector<Read> reads)
	{
		createPackSet(reads);
	}

	public Iterator<Pack> iterator()
	{
		return packs.iterator();
	}

	public int size()
	{
		return packs.size();
	}

//	public Pack getPackAt(int index)
//	{
//		return packs.get(index);
//	}

	private void createPackSet(Vector<Read> reads)
	{
		int readCount = 0;

		for (Read read: reads)
		{
			boolean added = false;

			// Can this read be added to any of the existing pack lines?
			for (Pack pack: packs)
			{
				if (added = pack.addRead(read))
					break;
			}

			// If not, create a new pack and add it there
			if (added == false)
			{
				Pack newPack = new Pack();
				newPack.addRead(read);

				added = packs.add(newPack);
			}

			if (added)
				readCount++;
		}

		System.out.println("Added " + readCount + " reads over " + packs.size() + " pack lines");
	}

	/**
	 * Returns a byte array containing sequence information (or -1 for no data)
	 * for the given line between the points start and end.
	 */
	public byte[] getValues(int line, int start, int end)
	{
		Pack pack = packs.get(line);

		return pack.getValues(start, end);
	}
}