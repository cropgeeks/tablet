// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.*;

import scri.commons.gui.*;

class CoverageCanvas extends TrackingCanvas
{
	private BasicStroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT,
		BasicStroke.JOIN_MITER, 10, new float[] { 5,2 }, 0);

	private int h = 25;

	CoverageCanvas()
	{
		setPreferredSize(new Dimension(0, h + 5));
	}

	void setAssemblyPanel(AssemblyPanel aPanel)
	{
		rCanvas = aPanel.readsCanvas;
		final ScaleCanvas sCanvas = aPanel.scaleCanvas;

		// Simple mouse listeners to update the base position on mouse overs
		addMouseListener(new MouseAdapter()
		{
			public void mouseExited(MouseEvent e)
			{
				sCanvas.setMouseBase(null);
			}
		});

		addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseMoved(MouseEvent e)
			{
				int xIndex = ((rCanvas.pX1 + e.getX()) / rCanvas.ntW) + offset;
				sCanvas.setMouseBase(xIndex);
			}
		});
	}

	void setContig(Contig contig)
	{
		if (contig != null)
		{
			offset = contig.getVisualStart();

			setToolTipText(
				RB.format("gui.viewer.OverviewCanvas.coverageTT",
				TabletUtils.nf.format(DisplayData.getAveragePercentage()),
				TabletUtils.nf.format(DisplayData.getAverageCoverage()),
				TabletUtils.nf.format(DisplayData.getMaxCoverage())));
		}
	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;

		int ntW = rCanvas.ntW;
		int xS = rCanvas.xS;
		int xE = rCanvas.xE;

		int[] coverage = DisplayData.getCoverage();
		int maxCoverage = DisplayData.getMaxCoverage();
		float avgCoverage = DisplayData.getAverageCoverage();

		for (int i = xS; i <= xE; i++)
		{
			float percent = coverage[i] / (float) maxCoverage;
			int barHeight = (int) (percent * h);

			// Work out an intensity value for it (0-255 gives light shades too
			// close to white, so adjust the scale to 25-255)
			int alpha = 25 + (int) (((255-25) * (255*percent)) / 255f);
			g.setColor(new Color(70, 116, 162, alpha));

			// Then fill a bar for it
			g.fillRect(i * ntW, 0, ntW, barHeight);
		}

		// Overlay the average value across all the data
		float percent = avgCoverage / (float) maxCoverage;
		int avgY = (int) (percent * h);
		g.setColor(new Color(45, 100, 162));
		g.setStroke(dashed);
		g.drawLine(x1, avgY, x2, avgY);
	}
}