package av.data;

import java.util.*;

import av.data.cache.*;

public class Assembly
{
	private IReadCache cache;

	private Vector<Contig> contigs = new Vector<Contig>();

	public Assembly()
	{
	}

	public void setReadCache(IReadCache cache)
		{ this.cache = cache; }

	public Vector<Contig> getContigs()
	{
		return contigs;
	}


	public void print()
	{
		System.out.println("Assembly:");

		for (Contig contig: contigs)
			contig.print(cache);
	}
}