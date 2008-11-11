package av.data;

import java.util.*;

import av.data.cache.*;

public class Assembly
{
	private IDataCache cache;

	private Vector<Contig> contigs = new Vector<Contig>();

	public Assembly()
	{
	}

	public void setDataCache(IDataCache cache)
		{ this.cache = cache; }

	public Vector<Contig> getContigs()
	{
		return contigs;
	}
}