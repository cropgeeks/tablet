// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis;

import scri.commons.gui.*;

import tablet.data.*;

/**
 * Creates the PairedStackSet which can be used to view paired-end read data.
 */
public class PairedStackCreator extends SimpleJob
{
	private PairedStack stackSet;
	private Contig contig;
	
	public PairedStackCreator(Contig contig)
	{
		this.contig = contig;
		stackSet = new PairedStack();
	}

	public void runJob(int jobIndex) throws Exception
	{
		long s = System.currentTimeMillis();

		// Use the number of reads as a count of how much work will be done
		maximum += contig.readCount();

		for(Read read: contig.getReads())
		{
			if(read instanceof MatedRead)
			{
				// Search for its pair and set up the link between them
				MatedRead matedRead = (MatedRead) read;

				if(matedRead.getPair() == null)
				{
					ReadPair readPair = new ReadPair();
					readPair.addRead(matedRead);
					stackSet.addPairedStack(readPair);
				}
				else
					addPairToStack(matedRead);
			}
			else
			{
				ReadPair readPair = new ReadPair();
				readPair.addRead(read);
				stackSet.addPairedStack(readPair);
			}
		}

		contig.setPairedStackSet(stackSet);

		long e = System.currentTimeMillis();
	}

	private void addPairToStack(MatedRead matedRead)
	{
		// If this is the first in the pair
		if (matedRead.getStartPosition() < matedRead.getMatePos())
		{
			ReadPair readPair = new ReadPair();
			readPair.addRead(matedRead);
			// If it has a valid paired read
			if (matedRead.getMatePos() > matedRead.getEndPosition())
				readPair.addRead(matedRead.getPair());
			stackSet.addPairedStack(readPair);
			// In the case where the paired reads overlap, assign the second
			// read to a new paired stack in the view.
			if (matedRead.getMatePos() < matedRead.getEndPosition())
			{
				ReadPair stack = new ReadPair();
				stack.addRead(matedRead.getPair());
				stackSet.addPairedStack(stack);
			}
		}
	}

	public String getMessage()
	{
		return RB.format("analysis.PackSetCreator.status", stackSet.size());
	}

}