package tablet.analysis;

import scri.commons.gui.*;

import tablet.data.*;

public class PairedPackSetCreator extends SimpleJob
{
	private PackSet packSet;
	private Contig contig;
	private boolean added, pairAdded;
	private int dataS;

	private int startPos, rowIndex, startRow;

	public PairedPackSetCreator(Contig contig, Assembly assembly)
	{
		this.contig = contig;
		packSet = new PackSet();
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
				MatedRead matedRead = (MatedRead) read;

				// Add the pair to an existing pack
				addToExistingPack(matedRead);

				// If that wasn't possible, add to a new pack.
				if(added == false)
					addToNewPack(matedRead);
			}
			// Add the single-end read to the packset.
			else
			{
				// Add the read to an existing pack
				addToPackInPackSet(read);
				// If that wasn't possible, add to a new pack.
				if(added == false)
					createPackForRead(read);
			}

			startPos = read.getStartPosition();

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

	/**
	 * Loop over existing packs attempting to add both the read and its pair to
	 * each pack, halting the loop if we are successful.
	 */
	private void addToExistingPack(MatedRead pairedRead)
	{
		for (int i = startRow; i < packSet.size(); i++)
		{
			// Try to add the first read in the pair, must be either the first read from a pair in the contig, or the only read from the pair in the contig
			if (canAddToExistingPack(pairedRead, i))
			{
				rowIndex = i;
				
				if(pairedRead.getPair() == null)
					break;

				// Try to add the second read in the pair
				if (!(pairAdded = packSet.get(i).addRead(pairedRead.getPair())))
				{
					for(int j=0; j < packSet.size(); j++)
					{
						pairAdded = packSet.get(j).addRead(pairedRead.getPair());
						if(pairAdded)
							break;
					}
					if(!pairAdded)
						createPackForRead(pairedRead.getPair());
				}
				break;
			}
		}
	}

	private void addToPackInPackSet(Read read)
	{
		for(int i = startRow; i < packSet.size(); i++)
		{
			added = packSet.get(i).addRead(read);
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
	private void addToNewPack(MatedRead pairedRead)
	{
	
		if (canAddToNewPack(pairedRead))
		{
			Pack newPack = new Pack();
			newPack.addRead(pairedRead);
			added = true;

			if(pairedRead.getPair() != null)
				pairAdded = newPack.addRead(pairedRead.getPair());

			packSet.addPack(newPack);

			if(pairedRead.getPair() == null)
				return;

			if (!pairAdded)
			{
				for(int i=startRow; i < packSet.size(); i++)
				{
					pairAdded = packSet.get(i).addRead(pairedRead.getPair());
					if(pairAdded)
						break;
				}
				if(!pairAdded)
					createPackForRead(pairedRead.getPair());
			}

			rowIndex = packSet.size()-1;
		}
	}

	private void createPackForRead(Read read)
	{
		Pack newPack = new Pack();
		newPack.addRead(read);
		packSet.addPack(newPack);
		rowIndex = packSet.size()-1;
	}

	private boolean canAddToNewPack(MatedRead matedRead)
	{
		return (matedRead.getStartPosition() < matedRead.getMatePos() || (matedRead.getPair() == null && matedRead.getMatePos() < dataS)) || !matedRead.isMateContig();
	}

	private boolean canAddToExistingPack(MatedRead matedRead, int i)
	{
		return (canAddToNewPack(matedRead)) && (added = packSet.get(i).addRead(matedRead));
	}

	public String getMessage()
	{
		return RB.format("analysis.PackSetCreator.status", packSet.size());
	}
}
