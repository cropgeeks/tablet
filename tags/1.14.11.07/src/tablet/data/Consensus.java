// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data;

import java.io.*;

import static tablet.data.Sequence.*;
import tablet.data.cache.*;

/** The consensus sequence for a contig. */
public class Consensus //extends Sequence
{
	private static ConsensusFileCache cache;

	private Sequence consensus;
	private int cacheID = -1;

	// Base quality information, one byte per nucleotide base
//	private byte[] bq;

	private int unpaddedLength;
	private int length;

	/** Constructs a new, empty consensus sequence. */
	public Consensus()
	{
	}

	/** To be used by UNIT TESTS ONLY to avoid disk caching **/
	public Consensus(String str)
	{
		consensus = new Sequence(str);

		length = str.length();
		calculateUnpaddedLength();
	}

	public int length()
		{ return length; }

	/**
	 * Returns the unpadded length of this consensus sequence.
	 * @return the unpadded length of this consensus sequence
	 */
	public int getUnpaddedLength()
		{ return unpaddedLength; }

	/**
	 * Sets the base score qualities for this consensus.
	 * @param bq the array of base quality scores (one per base of the consensus
	 * with -1 for bases with no score).
	 */
	public void setBaseQualities(byte[] bq)
		{ /*this.bq = bq;*/ }

	public static ConsensusFileCache getCache()
	{
		return cache;
	}

	// Returns the offset (into the cache file) where this consensus's data is
	public long getCacheOffset()
	{
		return cache.getOffset(cacheID);
	}

	public void setCacheOffset(int cacheID, long offset, int length)
	{
		this.cacheID = cacheID;
		this.length = length;

		cache.addIndexEntry(offset, length);
	}

	/**
	 * Returns true if this consensus sequence contains quality scores for its
	 * bases.
	 */
	public boolean hasBaseQualities()
		{ return false; /*return bq != null;*/ }

	/**
	 * Returns an array of base quality data, starting at start and ending at
	 * end. These values may be outside of the actual start and end values for
	 * the consensus, in which case -1 will be returned for those positions.
	 * @param start the starting index
	 * @param end the ending index (inclusive)
	 * @return an array of base quality data
	 */
	public byte[] getBaseQualityRange(int start, int end)
	{
		byte[] data = new byte[end-start+1];

/*		int i = 0, d = 0;
		int length = bq.length;

		// Pre sequence data
		for (i = start; i < 0 && i <= end; i++, d++)
			data[d] = -1;

		// Sequence data
		for (i = i; i <= end && i < length; i++, d++)
			data[d] = bq[i];

		// Post sequence data
		for (i = i; i <= end; i++, d++)
			data[d] = -1;
*/
		return data;
	}

	/**
	 * Returns an array of sequence data, starting at start and ending at end.
	 * These values may be outside of the actual start and end values for the
	 * consensus, in which case -1 will be returned for those positions.
	 * @param start the starting index
	 * @param end the ending index (inclusive)
	 * @return an array of sequence data
	 */
	public byte[] getRange(int start, int end)
	{
		byte[] data = new byte[end-start+1];

		int i = 0, d = 0;
		int length = length();

		// Pre sequence data
		for (i = start; i < 0 && i <= end; i++, d++)
			data[d] = -1;

		// Sequence data
		for (i = i; i <= end && i < length; i++, d++)
			data[d] = consensus.getStateAt(i);

		// Post sequence data
		for (i = i; i <= end; i++, d++)
			data[d] = -1;

		return data;
	}

	public byte getStateAt(int pos)
	{
		if (pos < 0 || pos >= length)
			return -1;

		return consensus.getStateAt(pos);
	}

	/**
	 * Returns a string representation of this sequence.
	 * @return a string representation of this thread
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++)
		{
			byte state = consensus.getStateAt(i);
			sb.append(consensus.getDNA(state));
		}

		return sb.toString();
	}

	public void appendSequence(String string)
		throws Exception
	{
		if (cacheID == -1)
			cacheID = cache.createNewEntry();

		cache.appendSequence(string);
	}

	public void closeSequence()
		throws Exception
	{
		length = cache.closeSequence();
		calculateUnpaddedLength();
	}

	private void calculateUnpaddedLength()
	{
		getSequence();

		// Store the sequence length (ignoring pads)
		for (int i = 0; i < length; i++)
			if (consensus.getStateAt(i) != P)
				unpaddedLength++;
	}

	public Sequence getSequence()
	{
		if (consensus == null)
		{
			try
			{
				// cacheID will be -1 if there is NO reference data loaded (the
				// Contig and Consensus classes still exist; they're just empty)
				if (cacheID == -1)
					return new Sequence();

				// Otherwise, grab the consensus data from the cache
				consensus = cache.getSequence(this, cacheID);
			}
			catch (Exception e)
			{
				// TODO: Critical error if this happens...
				System.out.println("getSequence() CRITICAL");
				e.printStackTrace();
			}
		}

		return consensus;
	}

	/**
	 * Used to "forget" (and hopefully garbage collect the sequence). Should
	 * only be called by ConsensusCache.
	 */
	public void forgetSequence()
	{
		consensus = null;
	}

	public static void prepareCache(File cacheFile)
		throws Exception
	{
		cache = new ConsensusFileCache(cacheFile);
	}
}