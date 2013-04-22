// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.analysis;

import java.util.ArrayList;
import tablet.data.*;
import tablet.data.cache.*;

public class PairSearcher
{
	private Contig contig;

	public PairSearcher(Contig contig)
	{
		this.contig = contig;
	}

	/**
	 * Search for a read's pair within the current contig (or current
	 * bam window of the contig in the case of paired-end bam) Can only be used
	 * in situations where the reads haven't been sorted yet.
	 */
	public Read search(MatedRead read)
		throws Exception
	{
		if (read.isMateContig() == false)
			return null;

		String name = Assembly.getReadName(read);

		ArrayList<Integer> potentialMates = Assembly.getReadsByName(name);

		Read potentialMate = null;
		int cacheOffset = contig.getCacheOffset();
		int readCount = contig.getReads().size();

		for (Integer potentialMateID : potentialMates)
		{
			// We can only pair up if we've found a match in the same contig...
			if (potentialMateID < cacheOffset || potentialMateID > cacheOffset+readCount)
				continue;

			potentialMate = contig.getReads().get(potentialMateID-cacheOffset);

			// ... with the same EXPECTED start position for the mate
			if (read.getMatePos() == potentialMate.s())
				return potentialMate;
		}

		return null;
	}



	/* 22/11/10 The code below is for use in situations where the DB lookup
		can't be used to find pairs. This is mainly any usage situation where
		the reads have already been sorted, such as any post-packing / stacking
		situation
	*/

	/**
	 * Binary search for a read's pair within the current contig (or current
	 * bam window of the contig in the case of paired-end bam).
	 */
	public Read searchForPair(String name, int pos)
		throws Exception
	{
		int low = 0;
		int high = contig.getReads().size() - 1;

		while (high >= low)
		{
			// Get mid while avoiding potential integer overflow
			int mid = low + ((high - low) / 2);
			int startPos = contig.getReads().get(mid).s();

			if (startPos < pos)
				low = mid + 1;

			else if (startPos > pos)
				high = mid - 1;

			else
				return refinePairSearch(mid, name, pos);
		}
		return null;
	}

	/**
	 * There is a potential for more than one read mapping to a position, as
	 * such we need to refine the search by searching linearly in both directions.
	 */
	private Read refinePairSearch(int index, String name, int pos)
		throws Exception
	{
		Read read = null;
		read = linearSearch( name, pos, index, -1);
		if(read != null)
			return read;

		read = linearSearch(name, pos, index, 1);
		if(read != null)
			return read;

		return null;
	}

	private Read linearSearch(String name, int pos, int index, int loopModifier)
		throws Exception
	{
		boolean prefixNameCase = name.endsWith(":1") || name.endsWith(":2");
		String prefixName = name.substring(0, name.length()-2);

		Read mate = contig.getReads().get(index);
		String mateName = Assembly.getReadName(mate);

		while (mate != null && pos == mate.s() && index > 0)
		{
			if (mateName.equals(name))
				return mate;

			// Deal with paired end reads which have had pair information encoded in the names of reads
			if (prefixNameCase)
			{
				if(mateName.substring(0, mateName.length()-2).equals(prefixName))
					return mate;
			}

			index += loopModifier;
			mate = contig.getReads().get(index);
			mateName = Assembly.getReadName(mate);
		}

		return null;
	}
}