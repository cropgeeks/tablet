// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.data.cache;

import java.io.*;
import java.util.*;

import tablet.data.*;

import scri.commons.file.*;

public class ConsensusFileCache extends TabletCache
{
	private ArrayList<Index> index = new ArrayList<Index>();

	private Consensus current = null;

	// When writing, how many bytes have been written to the cache?
	private long byteCount = 0;

	// Tracks the actual length (in nucleotides, not bytes) of the current
	// sequence being written to the cache
	private int ntLength = 0;

	// Because the byte[] data being written requires two bases at a time, we
	// need to track "remainder" bases from any sequences handed in with an odd
	// number of bases
	private String remainder = "";


	public ConsensusFileCache(File cacheFile)
	{
		this.cacheFile = cacheFile;

		cacheFile.deleteOnExit();
	}

	@Override
	public void openForReading()
		throws IOException
	{
		if (out != null)
			out.flush();

		if (rnd == null)
			rnd = new BufferedRandomAccessFile(cacheFile, "r", 1024);
	}

	@Override
	public void openForWriting()
		throws IOException
	{
		if (out == null)
			out = new DataOutputStream(new BufferedOutputStream(
				new FileOutputStream(cacheFile, byteCount != 0)));
	}

	public int createNewEntry()
		throws IOException
	{
		openForWriting();
		ntLength = 0;

		// Store the current filepointer information in the index
		index.add(new Index(byteCount));

		// And return what will be the ID for the next sequence to be stored
		return index.size()-1;
	}

	/**
	 * Appends the next section of an overall consensus Sequence (passed to this
	 * method as a short string) into the cache.
	 */
	public void appendSequence(String str)
		throws Exception
	{
		// Sequence can only deal in even numbers for the nibbles it stores, so
		// we need to ensure that we never create one with an odd sequence
		// length. Any "remainder" must be held until the next portion is
		// appended, or written at the very end in closeSequence().

		ntLength += str.length();
		str = remainder + str;

		if (str.length() % 2 == 0)
		{
			// Input is ABCDEF, then write ABCDEF
			write(new Sequence(str));
			remainder = "";
		}
		else
		{
			// Input is ABCDEFG, then write ABCDEF ([G] = remainder)
			write(new Sequence(str.substring(0, str.length()-1)));
			remainder = str.substring(str.length()-1);
		}
	}

	public int closeSequence()
		throws Exception
	{
		if (remainder.length() > 0)
		{
			write(new Sequence(remainder));
			remainder = "";
		}

		// Finalize the index with the length of all the data written for the
		// current sequence
		Index i = index.get(index.size()-1);
		i.length = (int) (byteCount - i.offset);

		return ntLength;
	}

	private void write(Sequence seq)
		throws Exception
	{
		byte[] data = seq.getRawData();

		out.write(data);

		byteCount += data.length;
	}

	public Sequence getSequence(Consensus parent, int id)
		throws Exception
	{
		openForReading();

		if (current != null && current != parent)
			current.forgetSequence();
		current = parent;

		// Jump to the required OFFSET and read LENGTH bytes
		rnd.seek(index.get(id).offset);
		byte[] data = new byte[index.get(id).length];
		int count = rnd.read(data);

		Sequence sequence = new Sequence();
		sequence.setRawData(data);

		return sequence;
	}

	// Represents an "index" to an entry in the cache, storing information on
	// the offset (in bytes) to find this entry, and also its length
	private static class Index
	{
		long offset;
		int length;

		Index(long offset)
		{
			this.offset = offset;
		}
	}
}