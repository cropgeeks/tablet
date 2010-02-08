// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

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

	private String cacheID;
	private static IReadCache cache;

	private ArrayList<Contig> contigs = new ArrayList<Contig>();

	private static boolean isBam = false;
	private static boolean hasCigar = false;

	/** Constructs a new, empty assembly. */
	public Assembly(String cacheID)
	{
		this.cacheID = cacheID;
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
	 * Returns the unique ID to be used with any associated cache objects.
	 */
	public String getCacheID()
		{ return cacheID; }

	/**
	 * Initializes the vector of contigs to be at least this size by default.
	 * @param size the initial size of the vector
	 */
	public void setContigsSize(int size)
		{ contigs = new ArrayList<Contig>(size); }

	public Iterator<Contig> iterator()
		{ return contigs.iterator(); }

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
	 * Returns the size of this assembly, that is, the number of contigs within
	 * it.
	 * @return the size of this assembly
	 */
	public int size()
		{ return contigs.size(); }

	/**
	 * Returns the contig at the given index location.
	 * @return the contig at the given index location
	 * @throws ArrayIndexOutOfBoundsException if the index is out of range
	 * (index < 0 || index >= size())
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
	public static ReadMetaData getReadMetaData(Read read, boolean dataOnly)
	{
		return cache.getReadMetaData(read.getID(), dataOnly);
	}

	/**
	 * Returns a reference to the read cache being used by this assembly.
	 * @return a reference to the read cache being used by this assembly
	 */
	public IReadCache getCache()
		{ return cache; }

	/**
	 * Returns true if this assembly holds data from a BAM assembly file.
	 * @return true if this assembly holds data from a BAM assembly file
	 */
	public static boolean isBam()
		{ return isBam; }

	public void setAsBamAssembly()
		{ isBam = true; }

	public static boolean hasCigar()
		{ return hasCigar; }

	public void setHasCigar()
		{ hasCigar = true; }
}