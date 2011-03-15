// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis;

import scri.commons.gui.*;

import tablet.data.*;

public class PairedPackCreator extends SimpleJob
{
	private PairedPack pack;
	private Contig contig;
	private boolean added, pairAdded;
	private int dataS;

	private int startPos, rowIndex, startRow;

	public PairedPackCreator(Contig contig, Assembly assembly)
	{
		this.contig = contig;
		pack = new PairedPack();
		dataS = contig.getVisualStart();
		startPos = rowIndex = 0;
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

			startRow = 0;

			if(read.getStartPosition() == startPos)
				startRow = rowIndex+1;
			
			added = pairAdded = false;

			ReadMetaData rmd = Assembly.getReadMetaData(read, false);
			if(read instanceof MatedRead && rmd.getIsPaired() && rmd.getMateMapped())
			{
				MatedRead mRead = (MatedRead) read;

				// Add the pair to an existing pack
				addToExistingPackRow(mRead);

				// If that wasn't possible, add to a new pack.
				if(added == false)
					addToNewPackRow(mRead);
			}
			// Add the single-end read to the packset.
			else
			{
				// Add the read to an existing pack
				addToPackRowInPack(read);
				// If that wasn't possible, add to a new pack.
				if(added == false)
					createPackRowForRead(read);
			}

			startPos = read.getStartPosition();

			progress++;
		}

		// Trim the packs down to size once finished
		for (PackRow packRow: pack)
			packRow.trimToSize();
		
		pack.trimToSize();

		contig.setPairedPack(pack);

		long e = System.currentTimeMillis();
		System.out.println("Packed data in " + (e-s) + "ms");
	}

	/**
	 * Loop over existing packs attempting to add both the read and its pair to
	 * each pack, halting the loop if we are successful.
	 */
	private void addToExistingPackRow(MatedRead read)
	{
		for (int i = startRow; i < pack.size(); i++)
		{
			// Try to add the first read in the pair, must be either the first read from a pair in the contig, or the only read from the pair in the contig
			if (canAddToExistingPackRow(read, i))
			{
				rowIndex = i;
				
				if(read.getPair() == null)
					break;

				// Try to add the second read in the pair
				if (!(pairAdded = pack.get(i).addRead(read.getPair())))
				{
					for(int j=0; j < pack.size(); j++)
					{
						pairAdded = pack.get(j).addRead(read.getPair());
						if(pairAdded)
							break;
					}
					if(!pairAdded)
						createPackRowForRead(read.getPair());
				}
				break;
			}
		}
	}

	private void addToPackRowInPack(Read read)
	{
		for(int i = startRow; i < pack.size(); i++)
		{
			added = pack.get(i).addRead(read);
			if(added)
			{
				rowIndex = i;
				break;
			}
		}
	}

	/**
	 * Attempt to add reads from the pair which were not added to an existing pack
	 * to a new pack.
	 */
	private void addToNewPackRow(MatedRead read)
	{
	
		if (canAddToNewPackRow(read))
		{
			PairedPackRow packRow = new PairedPackRow();
			packRow.addRead(read);
			added = true;

			if(read.getPair() != null)
				pairAdded = packRow.addRead(read.getPair());

			pack.addPackRow(packRow);

			if(read.getPair() == null)
				return;

			if (!pairAdded)
			{
				for(int i=startRow; i < pack.size(); i++)
				{
					pairAdded = pack.get(i).addRead(read.getPair());
					if(pairAdded)
						break;
				}
				if(!pairAdded)
					createPackRowForRead(read.getPair());
			}

			rowIndex = pack.size()-1;
		}
	}

	private void createPackRowForRead(Read read)
	{
		PairedPackRow packRow = new PairedPackRow();
		packRow.addRead(read);
		pack.addPackRow(packRow);
		rowIndex = pack.size()-1;
	}

	private boolean canAddToNewPackRow(MatedRead matedRead)
	{
		return (matedRead.getStartPosition() < matedRead.getMatePos() || 
				(matedRead.getPair() == null && matedRead.getMatePos() < dataS))
				|| !matedRead.isMateContig();
	}

	private boolean canAddToExistingPackRow(MatedRead matedRead, int i)
	{
		return (canAddToNewPackRow(matedRead)) && (added = pack.get(i).addRead(matedRead));
	}

	public String getMessage()
	{
		return RB.format("analysis.PackSetCreator.status", pack.size());
	}
}