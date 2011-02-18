// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis;

import java.io.*;
import java.util.*;

import tablet.analysis.tasks.*;
import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.data.cache.*;
import tablet.gui.*;

import scri.commons.gui.*;

/**
 * Computes and fills the tablet.data.auxiliary.DisplayData object with the
 * values it needs to hold before a contig can be displayed to the screen.
 */
public class DisplayDataCalculator extends SimpleJob implements ITaskListener
{
	private boolean doAll;
	private Assembly assembly;
	private Contig contig;

	// The objects that will run calculations as part of this job
	private CoverageCalculator cc;
	private SimpleJob packCreator;

	// And the objects that will hold the results
	private IArrayIntCache paddedToUnpadded;
	private IArrayIntCache unpaddedToPadded;

	private int status = 0;

	public DisplayDataCalculator(Assembly assembly, Contig contig, boolean doAll)
	{
		this.assembly = assembly;
		this.contig = contig;
		this.doAll = doAll;

		if (doAll)
		{
			// Create any cache objects that will be needed
			int length = contig.getConsensus().length();

			// The padded->unpadded mapping array...
			if (Prefs.cacheMappings && length > 1000000)
			{
				File cache = new File(Prefs.cacheDir, "Tablet-" + assembly.getCacheID() + ".paddedmap");
				paddedToUnpadded = new ArrayIntFileCache(cache);
			}
			else
				paddedToUnpadded = new ArrayIntMemCache(length);

			// The unpadded->padded mapping array...
			if (Prefs.cacheMappings && length > 1000000)
			{
				File cache = new File(Prefs.cacheDir, "Tablet-" + assembly.getCacheID() + ".unpaddedmap");
				unpaddedToPadded = new ArrayIntFileCache(cache);
			}
			else
			 	unpaddedToPadded = new ArrayIntMemCache(length);
		}
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		if (assembly.getBamBam() != null)
		{
			status = 1;
			assembly.getBamBam().loadDataBlock(contig);
		}

		// TODO: Technically, these jobs could be run in parallel but it's
		// probably not worth it as 99% of the time they run instantly anyway

		// UPDATE: (05/02/2010) - Running mapping calculations in parallel was
		// actually slower than sequentially, because it had to create multiple
		// simultaneous disk caches (and the HDD didn't like doing that).

		if (okToRun && doAll)
		{
			// Compute mappings between unpadded and padded values
			BaseMappingCalculator bm = new BaseMappingCalculator(
				contig.getConsensus(), paddedToUnpadded, unpaddedToPadded);

			bm.addTaskListener(this);

			TaskManager.submit("BaseMappingCalculator", bm);
		}

		if (okToRun)
		{
			status = 2;

			// Compute per-base coverage across the contig
			cc = new CoverageCalculator(contig);
			cc.runJob(0);

			DisplayData.setCoverage(cc.getCoverage());
			DisplayData.setMaxCoverage(cc.getMaximum());
			DisplayData.setBaseOfMaximum(cc.getBaseOfMaximum());
			DisplayData.setAverageCoverage(cc.getAverageCoverage());
			DisplayData.setAveragePercentage(cc.getAveragePercentage());
		}

		if(okToRun && Assembly.isPaired())
		{
			maximum = contig.getReads().size();
			status = 3;
			long s = System.currentTimeMillis();
			pairReads();
			System.out.println("Paired reads in: " + (System.currentTimeMillis()-s));
		}

		if (okToRun)
		{
			// Sort the reads into order
			System.out.print("Sorting...");
			long s = System.currentTimeMillis();

			contig.getReads().trimToSize();
			Collections.sort(contig.getReads());
			contig.calculateOffsets(assembly);

			System.out.println((System.currentTimeMillis()-s) + "ms");
		}

		if (okToRun)
		{
			status = 4;
			packCreator = setupPackCreator();

			if(packCreator != null)
				packCreator.runJob(0);
		}
	}

	private SimpleJob setupPackCreator()
	{
		// If pack is selected
		if(Prefs.visPacked && !Prefs.visPaired)
			packCreator = new PackSetCreator(contig);

		// If pair pack is selected
		else if(Prefs.visPacked && Prefs.visPaired)
			packCreator = new PairedPackSetCreator(contig, assembly);

		// if pair stack is selected
		else if(!Prefs.visPacked && Prefs.visPaired)
			packCreator = new PairedStackCreator(contig);

		else if(!Prefs.visPacked && !Prefs.visPaired)
			packCreator = new PackSetCreator(contig);

		return packCreator;
	}

	private void pairReads()
		throws Exception
	{
		PairSearcher pairSearcher = new PairSearcher(contig);

		for (Read read: contig.getReads())
		{
			if (!okToRun)
				break;

			try
			{
				// Search for its pair and set up the link between them
				MatedRead matedRead = (MatedRead) read;

				// If this read has already had its mate set, skip it
				if(matedRead.getPair() == null)
				{
					ReadMetaData rmd = Assembly.getReadMetaData(read, false);
					if(!rmd.getMateMapped())
						continue;

					MatedRead foundPair = (MatedRead) pairSearcher.search(matedRead);
					if(foundPair != null)
					{
						matedRead.setPair(foundPair);
						foundPair.setPair(matedRead);
					}
				}
			}
			catch(ClassCastException e)
			{
//				e.printStackTrace();
			}
			progress++;

		}

	}

	public void cancelJob()
	{
		super.cancelJob();

		if (cc != null)
			cc.cancelJob();
		if (packCreator != null)
			packCreator.cancelJob();

		if (assembly.getBamBam() != null)
			assembly.getBamBam().getBamFileHandler().cancel();
	}

	public int getMaximum()
	{
		if (status == 4 && packCreator != null)
			maximum = packCreator.getMaximum();

		return maximum;
	}

	public int getValue()
	{
		if (status == 4 && packCreator != null)
			progress = packCreator.getValue();

		return progress;
	}

	public String getMessage()
	{
		switch (status)
		{
			case 1: return
				RB.format("analysis.DisplayDataCalculator.bamming",
				contig.readCount());
			case 2: return
				RB.getString("analysis.DisplayDataCalculator.coverage");
			case 3: return
					RB.format("analysis.DisplayDataCalculator.pairing", progress);
			case 4: return
				packCreator.getMessage();

			default:
				return "";
		}
	}

	public void taskCompleted(EventObject e)
	{
		if (e.getSource() instanceof BaseMappingCalculator)
		{
			BaseMappingCalculator bm = (BaseMappingCalculator) e.getSource();

			DisplayData.setMappingData(bm.getMappingData());
		}
	}
}