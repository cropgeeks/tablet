// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.viewer.colors.*;

class FeaturesCanvas extends TrackingCanvas
{
	private AssemblyPanel aPanel;

	private Contig contig;
	private Consensus consensus;

	private Dimension dimension = new Dimension();

	FeaturesCanvas()
	{
	}

	void setAssemblyPanel(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;
		rCanvas = aPanel.readsCanvas;
	}

	void setContig(Contig contig)
	{
		if (contig != null)
		{
			consensus = contig.getConsensus();

			prepareTracks(contig);
		}

		// Remove tablet.data references if nothing is going to be displayed
		else
			consensus = null;

		this.contig = contig;

		dimension = new Dimension(0, 20);

		setPreferredSize(dimension);
		revalidate();
	}

	private void prepareTracks(Contig contig)
	{
		try
		{
			// Set up the tracks for this contig
			VisualContig vContig = aPanel.getVisualContig();

			FeatureTrackCreator ftc = new FeatureTrackCreator(vContig, contig);
			ftc.runJob(0);
		}
		catch (Exception e) { e.printStackTrace(); }
	}

	public Dimension getPreferredSize()
		{ return dimension; }

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;

		if (consensus.length() == 0)
			return;

		offset = contig.getVisualStart();

		int ntW = rCanvas.ntW;
		int xS = rCanvas.xS;
		int xE = rCanvas.xE;

//		byte[] bq = consensus.getBaseQualityRange(xS+offset, xE+offset);

		g.drawString("features", 10, 10);
	}
}