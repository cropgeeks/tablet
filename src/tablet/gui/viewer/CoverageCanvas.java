// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

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
				int x = getMouseX(e);
				int ntIndex = rCanvas.getBaseForPixel(rCanvas.pX1 + x);

				sCanvas.setMouseBase(ntIndex);
			}
		});

		new ReadsCanvasDragHandler(aPanel, this);
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

		try
		{
			// The paint mechanism differs based on zoom level
			if (rCanvas.ntW > 1)
				paintNormal(g);
			else
				paintSuperZoom(g);

			paintAverage(g);
		}
		catch (Exception e)
		{
			System.out.println("CoverageCanvas: " + e);
//			repaint();
		}
	}

	private void paintAverage(Graphics2D g)
	{
		int maxCoverage = DisplayData.getMaxCoverage();
		float avgCoverage = DisplayData.getAverageCoverage();

		// Overlay the average value across all the data
		float percent = avgCoverage / (float) maxCoverage;
		int avgY = (int) (percent * h);
		g.setColor(new Color(45, 100, 162));
		g.setStroke(dashed);
		g.drawLine(x1, avgY, x2, avgY);
	}

	private void paintNormal(Graphics2D g)
	{
		float ntW = rCanvas.ntW;
		int xS = rCanvas.xS;
		int xE = rCanvas.xE;

		int[] coverage = DisplayData.getCoverage();
		int maxCoverage = DisplayData.getMaxCoverage();

			for (int i = xS; i <= xE; i++)
			{
				float percent = coverage[i] / (float) maxCoverage;
				int barHeight = (int) (percent * h);

				// Work out an intensity value for it (0-255 gives light shades too
				// close to white, so adjust the scale to 25-255)
				int alpha = 25 + (int) (((255-25) * (255*percent)) / 255f);
				g.setColor(new Color(70, 116, 162, alpha));

			// Then fill a bar for it
			g.fillRect((int)(i * ntW), 0, (int)ntW, barHeight);
		}
	}

	private void paintSuperZoom(Graphics2D g)
	{
		int[] coverage = DisplayData.getCoverage();
		int maxCoverage = DisplayData.getMaxCoverage();

		int basesPerPixel = Math.round(1 / rCanvas.ntW);
		int offset = rCanvas.offset;

		// For each pixel to be painted
		for (int x = rCanvas.pX1; x <= rCanvas.pX2; x++)
		{
			// Work out what the average coverage level is across this pixel
			// by looking at every base that maps to it (and adjust for offset)
			int base = rCanvas.getBaseForPixel(x) - offset;
			float averageValue = 0;
			float averageCount = 0;

			for (int i = 0; i < basesPerPixel; i++, averageCount++)
			{
				// Quit counting if we've reached the end of the data
				if (base+i >= coverage.length)
					break;

				averageValue += coverage[base+i];
			}

			float value = averageValue / averageCount;
			float percent = value / (float) maxCoverage;
			int barHeight = (int) (percent * h);

			// Work out an intensity value for it (0-255 gives light shades too
			// close to white, so adjust the scale to 25-255)
			int alpha = 25 + (int) (((255-25) * (255*percent)) / 255f);
			g.setColor(new Color(70, 116, 162, alpha));

			// Then fill a bar for it
			g.drawLine(x, 0, x, barHeight);
		}
	}
}