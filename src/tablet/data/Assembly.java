package tablet.data;

import java.util.*;

import tablet.data.cache.*;

/**
 * The top-level of Tablet's data structures, an assembly holds a list of
 * contigs. It also contains methods for accessing meta data on all the reads in
 * an assembly from whichever type of read cache has been used.
 */
public class Assembly implements Iterable<Contig>
{
	private String name;
	private IReadCache cache;

	private Vector<Contig> contigs = new Vector<Contig>();

	/** Constructs a new, empty assembly. */
	public Assembly()
	{
	}

	/**
	 * Sets the name for this assembly, which will usually be the name of the
	 * file that the assembly was loaded from.
	 * @param name the name for this assembly
	 */
	public void setName(String name)
		{ this.name = name; }

	/**
	 * Returns the name of this assembly.
	 * @return the name of this assembly
	 */
	public String getName()
		{ return name; }

	/**
	 * Initializes the vector of contigs to be at least this size by default.
	 * @param size the initial size of the vector
	 */
	public void setContigsSize(int size)
		{ contigs = new Vector<Contig>(size); }

	public Iterator<Contig> iterator()
		{ return contigs.iterator(); }

	/**
	 * Returns a count of the number of contigs held within this assembly.
	 * @return a count of the number of contigs held within this assembly
	 */
	public int contigCount()
		{ return contigs.size(); }

	/**
	 * Assigns the read cache for this assembly. All further lookups for read
	 * meta data will be performed using this cache object.
	 * @param cache the read cache to assign
	 */
	public void setReadCache(IReadCache cache)
		{ this.cache = cache; }

	/**
	 * Adds a new contig to this assembly.
	 * @param contig the contig to add
	 */
	public void addContig(Contig contig)
		{ contigs.add(contig); }

	/**
	 * Returns the contig at the given index location.
	 * @return the contig at the given index location
	 * @throws ArrayIndexOutOfBoundsException if the index is out of range
	 * (index < 0 || index >= contigCount())
	 */
	public Contig getContig(int index)
		throws ArrayIndexOutOfBoundsException
	{
		return contigs.get(index);
	}

	/**
	 * Finds and returns the read meta data for the given read.
	 * @return the read meta data for the given read
	 */
	public ReadMetaData getReadMetaData(Read read)
	{
		return cache.getReadMetaData(read.getID());
	}

	/**
	 * Returns a reference to the read cache being used by this assembly.
	 * @return a reference to the read cache being used by this assembly
	 */
	public IReadCache getCache()
		{ return cache; }
}