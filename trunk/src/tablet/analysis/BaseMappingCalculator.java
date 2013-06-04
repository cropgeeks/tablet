// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.analysis;

import java.util.*;

import tablet.analysis.tasks.*;
import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.data.cache.*;

/**
 * Calculates the padded->unpadded and unpadded->padded translation information
 * for a contig's consensus sequence.
 */
public class BaseMappingCalculator extends BackgroundTask
{
	private Consensus c;

	// Contains info to map from a padded to an unpadded position
	private IArrayIntCache paddedToUnpadded;
	// Contains info to map from an unpadded to a padded position
	private IArrayIntCache unpaddedToPadded;

	private MappingData mappingData;

	public BaseMappingCalculator(Consensus c, IArrayIntCache paddedToUnpadded, IArrayIntCache unpaddedToPadded)
	{
		this.c = c;
		this.paddedToUnpadded = paddedToUnpadded;
		this.unpaddedToPadded = unpaddedToPadded;
		mappingData = new MappingData(c);
	}

	public void run()
	{
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		Thread.currentThread().setName("BaseMappingCalculator");

		// Because the BaseMappingCalculator (might) be accessing files, it's
		// best to let any previous instances completely finish before starting
		if (previous != null)
			while (previous.isRunning())
				try { Thread.sleep(50); }
				catch (InterruptedException e) {}

		try
		{
			calculateMappings();

			if (okToRun)
			{
				paddedToUnpadded.openForReading();
				unpaddedToPadded.openForReading();
			}
		}
		catch (Throwable e)
		{
			System.out.println("BaseMappingCalculator: " + e);
			okToRun = false;
		}

		notifyAndFinish();
	}

	protected void doCleanup()
	{
		try
		{
			paddedToUnpadded.close();
			unpaddedToPadded.close();
		}
		catch (Exception e) {}
	}

	public IArrayIntCache getPaddedToUnpadded()
		{ return paddedToUnpadded; }

	public IArrayIntCache getUnpaddedToPadded()
		{ return unpaddedToPadded; }


	/**
	 * Calculate the mappings from padded to unpadded space and unpadded to
	 * padded space. Two ArrayLists are built up until we have the final set of
	 * data, then the data is put into the appropriate IArrayCache objects.
	 */
	private void calculateMappings()
		throws Exception
	{
		int padCount = 0;
		int length = c.length();
		boolean hasPad = false;

		Sequence cSeq = c.getSequence();

		ArrayList<Integer> padToUnpad = new ArrayList<>();
		ArrayList<Integer> unpadToPad = new ArrayList<>();

		// Loop over the consensus bases
		for (int baseCount =0; baseCount < length && okToRun; baseCount++)
		{
			// If the base isn't a pad
			if (cSeq.getStateAt(baseCount) != Sequence.P)
			{
				// If the previous base(s) was a pad
				if(hasPad)
				{
					// Add the appropriate base number to the ArrayList, with the
					// appropriate pad number.
					unpadToPad.add(baseCount-padCount);
					unpadToPad.add(padCount);
					hasPad = false;
				}
			}
			// If the base is a pad
			else
			{
				hasPad = true;
				// Add the appropriate base number to the ArrayList, with the
				// appropriate pad number.
				padToUnpad.add(baseCount);
				padToUnpad.add(++padCount);
			}
		}
		// Deals with case where last bases are padded.
		if(hasPad)
		{
			unpadToPad.add(length-padCount);
			unpadToPad.add(padCount);
		}

		// Call the method which sets up the mappings caches which will be used.
		setupMappingArrays(padToUnpad, unpadToPad);
	}

	private void setupMappingArrays(ArrayList<Integer> padToUnpad, ArrayList<Integer> unpadToPad)
		throws Exception
	{
		// Determine if we should disk cache or not
		if (padToUnpad.size() < 1000000)
			paddedToUnpadded = new ArrayIntMemCache(padToUnpad.size());
		if (unpadToPad.size() < 1000000)
			unpaddedToPadded = new ArrayIntMemCache(unpadToPad.size());

		// Create the cache and pass it to the MappingData object
		paddedToUnpadded.openForWriting();
		for (int i = 0; i < padToUnpad.size() && okToRun; i++)
			paddedToUnpadded.addValue(padToUnpad.get(i));

		// Create the cache and pass it to the MappindData object
		unpaddedToPadded.openForWriting();
		for(int i = 0; i < unpadToPad.size() && okToRun; i++)
			unpaddedToPadded.addValue(unpadToPad.get(i));

		if (okToRun)
		{
			mappingData.setPaddedToUnpaddedCache(paddedToUnpadded);
			mappingData.setUnpaddedToPaddedCache(unpaddedToPadded);
		}
	}

	public MappingData getMappingData()
	{
		return mappingData;
	}
}