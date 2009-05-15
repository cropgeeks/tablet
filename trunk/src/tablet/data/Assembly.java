package tablet.data;

import java.util.*;

import tablet.data.cache.*;

public class Assembly
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

	public void setReadCache(IReadCache cache)
		{ this.cache = cache; }

	public Vector<Contig> getContigs()
	{
		return contigs;
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