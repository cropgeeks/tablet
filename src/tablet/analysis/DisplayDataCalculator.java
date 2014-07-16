// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.analysis;

import java.io.*;
import java.util.*;

import tablet.analysis.Finder.*;
import tablet.analysis.tasks.*;
import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.data.auxiliary.Feature.*;
import tablet.data.cache.*;
import tablet.gui.*;

import scri.commons.gui.*;

/**
 * Computes and fills the tablet.data.auxiliary.DisplayData object with the
 * values it needs to hold before a contig can be displayed to the screen.
 */
public class DisplayDataCalculator extends SimpleJob implements ITaskListener
{
	private boolean doAll, doLoad;

	private Assembly assembly;
	private Contig contig;

	// The objects that will run calculations as part of this job
	private CoverageCalculator cc;
	private SimpleJob packCreator;

	// And the objects that will hold the results
	private IArrayIntCache paddedToUnpadded;
	private IArrayIntCache unpaddedToPadded;

	private int status = 0;
	private static final int LOADING = 1;
	private static final int INDEXING = 2;
	private static final int COVERAGE = 3;
	private static final int PAIRING = 4;
	private static final int PACKING = 5;

	private EnzymeHandler enzymeCalc = new EnzymeHandler();

	public DisplayDataCalculator(Assembly assembly, Contig contig, boolean doAll, boolean doLoad)
	{
		this.assembly = assembly;
		this.contig = contig;
		this.doAll = doAll;
		this.doLoad = doLoad;

		if (doAll)
		{
			// Create any cache objects that will be needed
			int length = contig.getConsensus().length();

			// The padded->unpadded mapping array...
			if (Prefs.cacheMappings && length > 1000000)
			{
				File cache = new File(Prefs.cacheFolder, "Tablet-" + assembly.getCacheID() + ".paddedmap");
				paddedToUnpadded = new ArrayIntFileCache(cache);
			}
			else
				paddedToUnpadded = new ArrayIntMemCache(length);

			// The unpadded->padded mapping array...
			if (Prefs.cacheMappings && length > 1000000)
			{
				File cache = new File(Prefs.cacheFolder, "Tablet-" + assembly.getCacheID() + ".unpaddedmap");
				unpaddedToPadded = new ArrayIntFileCache(cache);
			}
			else
			 	unpaddedToPadded = new ArrayIntMemCache(length);
		}
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		if (assembly.getBamBam() != null && doLoad)
		{
			status = LOADING;
			contig.clearCigarFeatures();
			assembly.getBamBam().loadDataBlock(contig);

			status = INDEXING;
			assembly.getBamBam().indexNames();
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

		if (okToRun && doLoad)
		{
			status = COVERAGE;

			// Compute per-base coverage across the contig
			cc = new CoverageCalculator(contig);
			cc.runJob(0);

			DisplayData.setCoverage(cc.getCoverage());
			DisplayData.setMaxCoverage(cc.getMaximum());
			DisplayData.setBaseOfMaximum(cc.getBaseOfMaximum());
			DisplayData.setAverageCoverage(cc.getAverageCoverage());
			DisplayData.setAveragePercentage(cc.getAveragePercentage());
		}

		if (okToRun && Assembly.isPaired() && doLoad)
		{
			maximum = contig.getReads().size();
			status = PAIRING;
			long s = System.currentTimeMillis();
			pairReads();
			System.out.println("Paired reads in: " + (System.currentTimeMillis()-s));
		}

		if (okToRun && doLoad)
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
			status = PACKING;
			packCreator = setupPackCreator();

			if (packCreator != null)
				packCreator.runJob(0);
		}

		enzymeCalc.setupEnzymeFinderTasks(contig);
	}

	private SimpleJob setupPackCreator()
	{
		// If PACK is selected
		if(Prefs.visPacked && !Prefs.visPaired)
			packCreator = new PackCreator(contig, false);

		// If PAIRED PACK is selected
		else if(Prefs.visPacked && Prefs.visPaired)
			packCreator = new PackCreator(contig, true);

		// If PAIRED STACK is selected
		else if(!Prefs.visPacked && Prefs.visPaired)
			packCreator = new PairedStackCreator(contig);

		// If STACK is selected
		else
			contig.setStack();

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
				if(matedRead.getMate() == null)
				{
					ReadMetaData rmd = Assembly.getReadMetaData(read, false);
					if(!rmd.getMateMapped())
						continue;

					MatedRead foundPair = (MatedRead) pairSearcher.search(matedRead);
					if(foundPair != null)
					{
						matedRead.setMate(foundPair);
						foundPair.setMate(matedRead);
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
		if (status == PACKING && packCreator != null)
			maximum = packCreator.getMaximum();

		return maximum;
	}

	public int getValue()
	{
		if (status == PACKING && packCreator != null)
			progress = packCreator.getValue();

		return progress;
	}

	public String getMessage()
	{
		switch (status)
		{
			case LOADING: return
				RB.format("analysis.DisplayDataCalculator.bamming",
				contig.readCount());

			case INDEXING: return
				RB.getString("analysis.DisplayDataCalculator.indexNames");

			case COVERAGE: return
				RB.getString("analysis.DisplayDataCalculator.coverage");

			case PAIRING: return
				RB.format("analysis.DisplayDataCalculator.pairing", progress);

			case PACKING: return
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