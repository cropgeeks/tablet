// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.analysis;

import java.util.*;

import tablet.data.*;
import tablet.gui.*;

import scri.commons.gui.*;

/**
 * Processes a contig to organise its reads into packs ready for display.
 */
public class PackCreator extends SimpleJob
{
	private Contig contig;
	private boolean isPairedPack;
	private Pack pack;
	private int startPos, rowIndex;

	// Tracks reads that overlap as they are the only ones were it is tricky to
	// determine if a read has already been added or not when dealing with a
	// mate later in the loop iteration
	private HashMap<Integer,Integer> overlaps = new HashMap<>();

	private int overlapCount = 0;
	private int addedCount = 0;

	public PackCreator(Contig contig, boolean isPairedPack)
	{
		this.contig = contig;
		this.isPairedPack = isPairedPack;

		// Create the new pack that will hold all the reads
		if (isPairedPack)
			pack = new PairedPack();
		else
			pack = new Pack();
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		long s = System.currentTimeMillis();

		// Use the number of reads as a count of how much work will be done
		maximum += contig.readCount();

		for (Read read: contig.getReads())
		{
			// Check for quit/cancel on the job...
			if (okToRun == false)
				return;

			switch (readType(read))
			{
				// Add just one read
				case 1: addRead(read);
						startPos = read.s();
						break;

				// Add both reads, but only ever track the start of the first
				case 2: addRead(read);
						startPos = read.s();
						addRead(((MatedRead)read).getMate());
						startPos = read.s();
						break;

				// Add both reads, all on one line
				case 3: addRead(new LinkedReads(read));
						startPos = read.s();
						break;
			}

			progress++;
		}

		// Trim the packs down to size once finished
		for (PackRow packRow: pack)
			packRow.trimToSize();
		pack.trimToSize();

		if (isPairedPack)
			contig.setPairedPack((PairedPack)pack);
		else
			contig.setPack(pack);

		long e = System.currentTimeMillis();
		System.out.println("Packed data in " + (e-s) + "ms");

		System.out.println("  Total reads packed: " + addedCount);
		System.out.println("  Overlapping pairs:  " + overlapCount);
	}

	// This method returns one of four values:
	//  0 - if the read is the 2nd read in a pair, and therefore has already
	//      been added in a previous pass
	//  1 - if the read should be added on its own
	//  2 - if the read has a pair, but they overlap
	//  3 - if the read has a pair, and they can be added on the same line
	private int readType(Read read)
	{
		// If we're only making a "normal" pack, or this read isn't even a
		// MatedRead, then just return 1 because we'll be adding it on its own
		if (isPairedPack == false || read instanceof MatedRead == false)
			return 1;

		MatedRead read1 = (MatedRead) read;

		// If the read doesn't have a mate assigned (out of contig, bam window
		// or singleton), then add it on its own
		if (read1.getMate() == null)
			return 1;

		MatedRead read2 = read1.getMate();

		// If the read's start is AFTER its mate, then it will already be packed
		if (read1.s() > read2.s())
			return 0;

		// If the reads overlap, then add them separately
		if (read1.s() <= read2.e() && read1.e() >= read2.s())
		{
			// But only if they haven't already been packed
			if (overlaps.containsKey(read1.getID()) || overlaps.containsKey(read2.getID()))
				return 0;

			overlaps.put(read1.getID(), 0);
			overlaps.put(read2.getID(), 0);

			overlapCount++;

			return 2;
		}

		// Otherwise, add them all on the same line (with a link)
		return 3;
	}

	private void addRead(Read read)
	{
		boolean added = false;
		int startRow = 0;

		if (read.s() == startPos)
			startRow = rowIndex+1;

		// Can this read be added to any of the existing pack lines?
		for (int i=startRow; i < pack.size(); i++)
		{
			if (added = addReadToPackRow(pack.get(i), read))
			{
				rowIndex = i;
				break;
			}
		}

		// If not, create a new pack and add it there
		if (added == false)
		{
			PackRow newRow = pack.addNewRow();
			addReadToPackRow(newRow, read);

			rowIndex = pack.size()-1;
		}
	}

	/**
	 * Attempts to add the read to a PackRow. It will only be added if it does
	 * not overlap with any reads already stored in this pack.
	 */
	private boolean addReadToPackRow(PackRow packRow, Read read)
	{
		if (packRow.getReads().isEmpty() || read.s() > packRow.getPositionE())
		{
			// Adding a normal Read
			if (read instanceof LinkedReads == false)
			{
				packRow.getReads().add(read);
				packRow.setPositionE(read.e() + Prefs.visPadReads);

				addedCount++;
			}

			// Adding a "fake" LinkedReads, which actually contains three parts
			else
			{
				LinkedReads obj = (LinkedReads) read;

				packRow.getReads().add(obj.read1);
				packRow.getReads().add(obj.link);
				packRow.getReads().add(obj.read2);
				packRow.setPositionE(obj.read2.e() + Prefs.visPadReads);

				addedCount += 2;
			}

			return true;
		}

		return false;
	}

	public String getMessage()
	{
		return RB.format("analysis.PackSetCreator.status", pack.size());
	}

	private static class LinkedReads extends Read
	{
		Read read1, read2;
		MateLink link;

		LinkedReads(Read read1)
		{
			this.read1 = read1;
			this.read2 = ((MatedRead)read1).getMate();

			link = new MateLink(read1.e() + 1);
			link.setLength(read2.s() - link.s());
		}

		public int s()
		{
			return read1.s();
		}
	}
}