// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data;

import java.sql.*;
import java.util.*;

import tablet.data.auxiliary.*;
import tablet.data.cache.*;
import tablet.io.*;

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
	private static ReadSQLCache nameCache;

	private ArrayList<Contig> contigs = new ArrayList<>();

	private BamBam bambam;
	private static boolean hasCigar;
	private static boolean isPaired = false;

	private static ArrayList<ReadGroup> readGroups = new ArrayList<>();

	private AssemblySummary statistics = new AssemblySummary();

	/** Constructs a new, empty assembly. */
	public Assembly(String cacheID)
	{
		this.cacheID = cacheID;

		hasCigar = false;

		Feature.clearTracking();
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
	{
		contig.setId(contigs.size());
		contigs.add(contig);
	}

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

	public boolean isUsingMemCache()
	{
		return cache instanceof ReadMemCache;
	}

	public static boolean hasCigar()
		{ return hasCigar; }

	public void setHasCigar()
		{ hasCigar = true; }

	public void setBamHandler(BamFileHandler bamHandler)
	{
		hasCigar = true;

		bambam = new BamBam(bamHandler);
	}

	/**
	 * Returns a reference to the BAM handler for this assembly, or null if one
	 * hasn't been defined (which means it's not a BAM assembly).
	 */
	public BamBam getBamBam()
		{ return bambam; }

	public void setNameCache(ReadSQLCache nameCache)
	{
		this.nameCache = nameCache;
	}

	/**
	 * Finds and returns the read meta data for the given read.
	 * @return the read meta data for the given read
	 */
	public static ReadNameData getReadNameData(Read read)
	{
		return nameCache.getReadNameData(read.getID());
	}

	public static String getReadName(Read read)
	{
		return nameCache.getReadName(read.getID());
	}

	public static ArrayList<ReadSQLCache.NameWrapper> getReadNameFinder(int startID, int limit)
	{
		return nameCache.getAllNames(startID, limit);
	}

	public static ArrayList<Integer> getReadsByName(String name) throws SQLException
	{
		return nameCache.getReadsByName(name);
	}

	public static boolean isPaired()
		{ return isPaired; }

	public static void setIsPaired(boolean isPaired)
		{ Assembly.isPaired = isPaired; }

	public static ArrayList<ReadGroup> getReadGroups()
		{ return readGroups; }

	public void setReadGroups(ArrayList<ReadGroup> readGroups)
		{ Assembly.readGroups = readGroups;	}

	public AssemblySummary getAssemblyStatistics()
		{ return statistics; }
}