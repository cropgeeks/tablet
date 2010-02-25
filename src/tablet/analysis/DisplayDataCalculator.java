// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis;

import java.io.*;
import java.util.concurrent.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.data.cache.*;
import tablet.gui.*;

import scri.commons.gui.*;

/**
 * Computes and fills the tablet.data.auxiliary.DisplayData object with the
 * values it needs to hold before a contig can be displayed to the screen.
 */
public class DisplayDataCalculator extends SimpleJob
{
	private boolean doAll;
	private Assembly assembly;
	private Contig contig;

	// The objects that will run calculations as part of this job
	private CoverageCalculator cc;
	private PackSetCreator ps;

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

			TaskManager.submit(bm);
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

		if (okToRun)
		{
			ps = new PackSetCreator(contig);
			ps.runJob(0);

			status = 3;
		}
	}

	public void cancelJob()
	{
		super.cancelJob();

		if (cc != null)
			cc.cancelJob();
		if (ps != null)
			ps.cancelJob();

		if (assembly.getBamBam() != null)
			assembly.getBamBam().getBamFileHandler().cancel();
	}

	public int getMaximum()
	{
		if (ps != null)
			maximum = ps.getMaximum();

		return maximum;
	}

	public int getValue()
	{
		if (ps != null)
			progress = ps.getValue();

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
			case 4: return
				ps.getMessage();

			default:
				return "";
		}
	}
}