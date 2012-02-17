// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;

import tablet.data.*;
import tablet.gui.viewer.colors.*;

class ConsensusCanvas extends TrackingCanvas
{
	private Contig contig;
	private Consensus consensus;

	private boolean hasBaseQualities;
	private int qualityH;

	// Low/high colour information used to draw the base quality scores
	private int[] c1;
	private int[] c2;

	private Dimension dimension = new Dimension();

	// A list of renderers that will perform further drawing once the main
	// canvas has been drawn
	LinkedList<IOverlayRenderer> overlays = new LinkedList<IOverlayRenderer>();

	ConsensusCanvas()
	{
		// Set up the base quality colours
		Color col1 = new Color(255, 120, 120); // low
		Color col2 = new Color(120, 255, 120); // high

		c1 = new int[] { col1.getRed(), col1.getGreen(), col1.getBlue() };
		c2 = new int[] { col2.getRed(), col2.getGreen(), col2.getBlue() };
	}

	void setAssemblyPanel(AssemblyPanel aPanel)
	{
		rCanvas = aPanel.readsCanvas;
		new ConsensusCanvasML(aPanel);
	}

	void setContig(Contig contig)
	{
		if (contig != null)
		{
			consensus = contig.getConsensus();
			hasBaseQualities = consensus.hasBaseQualities();
		}

		// Remove tablet.data references if nothing is going to be displayed
		else
			consensus = null;

		this.contig = contig;
	}

	void setDimensions()
	{
		qualityH = rCanvas.ntH/2 + 2;
		if (hasBaseQualities == false)
			qualityH = 0;

		dimension = new Dimension(0, (qualityH + /*rCanvas.ntH*/ (int)(Math.random()*10)) + 5);

		setPreferredSize(dimension);
		revalidate();
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

		float ntW = rCanvas._ntW;
		int ntH = rCanvas.ntH;
		int xS = rCanvas.xS;
		int xE = rCanvas.xE;
		int pixelsOnScreenX = rCanvas._pixelsOnScreenX;

		ReadScheme colors = rCanvas.colors;

		int y = 0;

		// Draw the quality scores
/*		if (hasBaseQualities)
		{
			byte[] bq = consensus.getBaseQualityRange(xS+offset, xE+offset);

			for (int i = 0, x = (ntW*xS); i < bq.length; i++, x += ntW)
			{
				if (bq[i] != -1)
				{
					// Determine the low/high intensity colour to use
					float f1 = (float) (100 - bq[i]) / 100;
					float f2 = (float) bq[i] / 100;

					g.setColor(new Color(
		          		(int) (f1 * c1[0] + f2 * c2[0]),
	          			(int) (f1 * c1[1] + f2 * c2[1]),
	          			(int) (f1 * c1[2] + f2 * c2[2])));

					g.fillRect(x, y, ntW, ntH/2);
				}
			}
		}
*/

		// Because only one consensus at a time is in memory, it is possible for
		// this drawing code to crap-out if another part of Tablet loads a
		// different consensus (eg, the finder or export coverage) because the
		// consensus data will no longer be available to this class
		try
		{
			y += qualityH;

			// "Old" style rendering (one base = one (or more) pixels)
			if (ntW > 1)
			{
				byte[] data = consensus.getRange(xS+offset, xE+offset);

				// Draw the consensus sequence
				for (int i = 0, x = (int)(ntW*xS); i < data.length; i++, x += ntW)
					if (data[i] != -1)
						g.drawImage(colors.getConsensusImage(data[i]), x, y, null);
			}
			// "Super-zoom" rendering
			else
			{
				// x = first pixel to draw on
				// loop will iterate over every pixel
				for (int i = 0, x = (int)(ntW*xS); i < pixelsOnScreenX; i++, x++)
				{
					int base = offset + xS + (int)(i / ntW);
					byte state = consensus.getStateAt(base);
					if (state != -1)
					{
						g.setColor(colors.getConsensusColor(state));
						g.drawLine(x, y, x, y+ntH);
					}
				}
			}

		}
		catch (Exception e) {};


		// Allow any overlays to be painted on top of it
		try
		{
			for (IOverlayRenderer renderer: overlays)
				renderer.render(g);
		}
		catch (ConcurrentModificationException e) {
			repaint();
		}
	}
}