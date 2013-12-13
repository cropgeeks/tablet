// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.analysis;

import scri.commons.gui.*;

import tablet.data.*;

/**
 * Creates the PairedStackSet which can be used to view paired-end read data.
 */
public class PairedStackCreator extends SimpleJob
{
	private PairedStack pairedStack;
	private Contig contig;

	public PairedStackCreator(Contig contig)
	{
		this.contig = contig;
		pairedStack = new PairedStack();
	}

	public void runJob(int jobIndex) throws Exception
	{
		// Use the number of reads as a count of how much work will be done
		maximum += contig.readCount();

		for(Read read: contig.getReads())
		{
			// Check for quit/cancel on the job...
			if (okToRun == false)
				return;

			if(read instanceof MatedRead)
			{
				// Search for its pair and set up the link between them
				MatedRead matedRead = (MatedRead) read;

				if(matedRead.getMate() == null)
				{
					PairedStackRow pairedStackRow = new PairedStackRow();
					pairedStackRow.addRead(matedRead);
					pairedStack.addPairedStackRow(pairedStackRow);
				}
				else
					addToPairedStackRow(matedRead);
			}
			else
			{
				PairedStackRow pairedStackRow = new PairedStackRow();
				pairedStackRow.addRead(read);
				pairedStack.addPairedStackRow(pairedStackRow);
			}
		}

		contig.setPairedStack(pairedStack);
	}

	private void addToPairedStackRow(MatedRead matedRead)
	{
		// If this is the first in the pair
		if (matedRead.s() < matedRead.getMatePos())
		{
			PairedStackRow pairedStackRow = new PairedStackRow();
			pairedStackRow.addRead(matedRead);

			// If it has a valid paired read
			if (matedRead.getMatePos() > matedRead.e())
				pairedStackRow.addRead(matedRead.getMate());

			pairedStack.addPairedStackRow(pairedStackRow);

			// In the case where the paired reads overlap, assign the second
			// read to a new paired stack in the view.
			if (matedRead.getMatePos() < matedRead.e())
			{
				PairedStackRow stack = new PairedStackRow();
				stack.addRead(matedRead.getMate());
				pairedStack.addPairedStackRow(stack);
			}
		}
	}

	@Override
	public String getMessage()
	{
		return RB.format("analysis.PackSetCreator.status", pairedStack.size());
	}

}