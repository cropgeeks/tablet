package tablet.analysis;

import tablet.data.*;

public class PairedPackSetCreator extends SimpleJob
{
	private PackSet packSet;
	private Contig contig;
	private boolean added, pairAdded;
	private int s;

	public PairedPackSetCreator(Contig contig, Assembly assembly)
	{
		this.contig = contig;
		packSet = new PackSet();
		s = assembly.getBamBam().getS();
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		long s = System.currentTimeMillis();

		// Use the number of reads as a count of how much work will be done
		maximum += contig.readCount();

		PairSearcher pairSearcher = new PairSearcher(contig);

		for (Read read: contig.getReads())
		{
			// Check for quit/cancel on the job...
			if (okToRun == false)
				return;
			
			added = pairAdded = false;

			if(read instanceof MatedRead)
			{
				// Search for its pair and set up the link between them
				MatedRead matedRead = (MatedRead) read;
				MatedRead foundPair = (MatedRead) pairSearcher.search(matedRead);
				if(foundPair != null)
				{
					matedRead.setPair(foundPair);
					foundPair.setPair(matedRead);
				}

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
		for (int i = 0; i < packSet.size(); i++)
		{
			// Try to add the first read in the pair, must be either the first read from a pair in the contig, or the only read from the pair in the contig
			if (canAddToExistingPack(pairedRead, i))
			{
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
		for(int i = 0; i < packSet.size(); i++)
		{
			added = packSet.get(i).addRead(read);
			if(added)
				break;
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
				for(int i=0; i < packSet.size(); i++)
				{
					pairAdded = packSet.get(i).addRead(pairedRead.getPair());
					if(pairAdded)
						break;
				}
				if(!pairAdded)
					createPackForRead(pairedRead.getPair());
			}
		}
	}

	private void createPackForRead(Read read)
	{
		Pack newPack = new Pack();
		newPack.addRead(read);
		packSet.addPack(newPack);
	}

	private boolean canAddToNewPack(MatedRead matedRead)
	{
		return (matedRead.getStartPosition() < matedRead.getMatePos() || matedRead.getMatePos() < s) || !matedRead.isMateContig();
	}

	private boolean canAddToExistingPack(MatedRead matedRead, int i)
	{
		return (canAddToNewPack(matedRead)) && (added = packSet.get(i).addRead(matedRead));
	}

}
