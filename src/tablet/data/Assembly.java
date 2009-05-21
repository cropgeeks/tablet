package tablet.data;

import java.util.*;

import tablet.data.cache.*;

public class Assembly implements Iterable<Contig>
{
	private String name;
	private IReadCache cache;

	private Vector<Contig> contigs = new Vector<Contig>();

	public Assembly()
	{
	}

	public void setName(String name)
		{ this.name = name; }

	public String getName()
		{ return name; }

	public Iterator<Contig> iterator()
		{ return contigs.iterator(); }

	public int contigCount()
		{ return contigs.size(); }

	public void setReadCache(IReadCache cache)
		{ this.cache = cache; }

	/* Adds a contig to this assembly. */
	public void addContig(Contig contig)
		{ contigs.add(contig); }

	public Contig getContig(int index)
		throws ArrayIndexOutOfBoundsException
	{
		return contigs.get(index);
	}

	public ReadMetaData getReadMetaData(Read read)
	{
		return cache.getReadMetaData(read.getID());
	}

	public void print()
	{
		System.out.println("Assembly:");

		for (Contig contig: contigs)
			contig.print(cache);
	}
}