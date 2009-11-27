// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.analysis;

import tablet.data.*;
import tablet.data.auxiliary.*;

import scri.commons.gui.*;

/**
 * Computes a fills the tablet.data.auxiliary.DisplayData object with the
 * values it needs to hold before a contig can be displayed to the screen.
 */
public class DisplayDataCalculator extends SimpleJob
{
	private Contig contig;

	// The objects that will run calculations as part of this job
	private BaseMappingCalculator bm;
	private CoverageCalculator cc;
	private PackSetCreator ps;

	public DisplayDataCalculator(Contig contig)
	{
		this.contig = contig;
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		// TODO: Technically, these jobs could be run in parallel but it's
		// probably not worth it as 99% of the time they run instantly anyway

		if (okToRun)
		{
			// Compute mappings between unpadded and padded values
			bm = new BaseMappingCalculator(contig.getConsensus());
			bm.runJob(0);

			DisplayData.setPaddedToUnpadded(bm.getPaddedToUnpaddedArray());
			DisplayData.setUnpaddedToPadded(bm.getUnpaddedToPaddedArray());
		}

		if (okToRun)
		{
			// Compute per-base coverage across the contig
			cc = new CoverageCalculator(contig);
			cc.runJob(0);

			DisplayData.setCoverage(cc.getCoverage());
			DisplayData.setMaxCoverage(cc.getMaximum());
			DisplayData.setAverageCoverage(cc.getAverage());
		}

		if (okToRun)
		{
			// Pack the reads into a packset (if it's not already been done)
			if (contig.isDataPacked() == false)
			{
				ps = new PackSetCreator(contig);
				ps.runJob(0);
			}
		}
	}

	public void cancelJob()
	{
		super.cancelJob();

		if (bm != null)
			bm.cancelJob();
		if (cc != null)
			cc.cancelJob();
		if (ps != null)
			ps.cancelJob();
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
		if (ps != null)
			return ps.getMessage();

		return RB.getString("analysis.DisplayDataCalculator.status");
	}
}