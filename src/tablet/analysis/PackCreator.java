// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.analysis;
import tablet.data.*;

import scri.commons.gui.*;

/**
 * Processes a contig to oganise its reads into packs ready for display.
 */
public class PackCreator extends SimpleJob
{
	private Contig contig;
	private Pack pack;
	private int startPos, rowIndex;

	public PackCreator(Contig contig)
	{
		this.contig = contig;
		pack = new Pack();
		startPos=rowIndex=0;
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		long s = System.currentTimeMillis();

		// Use the number of reads as a count of how much work will be done
		maximum += contig.readCount();

		boolean added = false;

		int startRow;

		for (Read read: contig.getReads())
		{
			added = false;

			startRow=0;
			// Check for quit/cancel on the job...
			if (okToRun == false)
				return;

			if(read.getStartPosition() == startPos)
				startRow = rowIndex+1;

			// Can this read be added to any of the existing pack lines?
			for(int i=startRow; i < pack.size(); i++)
			{
				if(added = pack.get(i).addRead(read))
				{
					rowIndex = i;
					break;
				}
			}

			// If not, create a new pack and add it there
			if (added == false)
			{
				PackRow newPackRow = new PackRow();
				newPackRow.addRead(read);

				pack.addPackRow(newPackRow);
				rowIndex = pack.size()-1;
			}

			startPos = read.getStartPosition();

			progress++;
		}

		// Trim the packs down to size once finished
		for (PackRow packRow: pack)
			packRow.trimToSize();
		pack.trimToSize();

		contig.setPack(pack);


		long e = System.currentTimeMillis();
		System.out.println("Packed data in " + (e-s) + "ms");
	}

	public String getMessage()
	{
		return RB.format("analysis.PackSetCreator.status", pack.size());
	}
}