// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis;
import tablet.data.*;

import scri.commons.gui.*;

/**
 * Processes a contig to oganise its reads into packs ready for display.
 */
public class PackSetCreator extends SimpleJob
{
	private Contig contig;
	private PackSet packSet;

	public PackSetCreator(Contig contig)
	{
		this.contig = contig;
		packSet = new PackSet(contig);
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		long s = System.currentTimeMillis();

		// Use the number of reads as a count of how much work will be done
		maximum += contig.readCount();

		boolean added = false;

		for (Read read: contig.getReads())
		{
			// Check for quit/cancel on the job...
			if (okToRun == false)
				return;

			// Can this read be added to any of the existing pack lines?
			for (Pack pack: packSet)
				if (added = pack.addRead(read))
					break;

			// If not, create a new pack and add it there
			if (added == false)
			{
				Pack newPack = new Pack();
				newPack.addRead(read);

				packSet.addPack(newPack);
			}

			progress++;
		}

		// Trim the packs down to size once finished
		for (Pack pack: packSet)
			pack.trimToSize();
		packSet.trimToSize();

		contig.setPackSet(packSet);


		long e = System.currentTimeMillis();
		System.out.println("Packed data in " + (e-s) + "ms");
	}

	public String getMessage()
	{
		return RB.format("analysis.PackSetCreator.status", packSet.size());
	}
}