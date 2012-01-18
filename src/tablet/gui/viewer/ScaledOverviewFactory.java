// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;

import tablet.data.*;
import tablet.gui.*;
import tablet.gui.viewer.colors.*;

class ScaledOverviewFactory extends OverviewBufferFactory
{
	private ReadsCanvas rCanvas;
	private IReadManager reads;

	private int ntOnCanvasY, oS, oE, overviewWidth;

	ScaledOverviewFactory(OverviewCanvas canvas, int w, int h, ReadsCanvas rCanvas)
	{
		super(canvas, w, h);
		this.rCanvas = rCanvas;

		// Make private references to certain values now, as they MAY change
		// while the buffer is still being created, which will create problems
		reads = rCanvas.reads;
		ntOnCanvasY = rCanvas.ntOnCanvasY;

		this.oS = canvas.oS;
		this.oE = canvas.oE;
		overviewWidth = (oE-oS+1);

		start();
	}

	public void run()
	{
		setPriority(Thread.MIN_PRIORITY);
		setName("ScaledOverview-BufferFactory");

		try { Thread.sleep(500); }
		catch (InterruptedException e) {}

		if (killMe)
			return;

		Graphics2D g = createBuffer();

		// Scaling factors
		float xScale = overviewWidth / (float) w;
		float yScale = ntOnCanvasY / (float) h;

		// Color scheme in use
		ReadScheme colors = rCanvas.colors;

		// Loop over every pixel that makes up the overview...
		for (int y = 0; y < h && !killMe; y++)
		{
			int dataY = (int) (y * yScale);

			for (int x = 0; x < w && !killMe; x++)
			{
				while (rCanvas.isRendering)
					try { Thread.sleep(5); }
					catch (InterruptedException e) {}

				// Working out where each pixel maps to in the data...
				int dataX = (int) (x * xScale) + oS;

				// Then drawing that data (or not)
				Read read = reads.getReadAt(dataY, dataX);
				if (read != null)
				{
					ReadMetaData rmd = Assembly.getReadMetaData(read, true);

					g.setColor(colors.getColor(rmd, dataX-read.getStartPosition()));
					g.drawLine(x, y, x, y);
				}
			}
		}

		// Remove any references to tablet.data objects we were tracking
		reads = null;

		if (!killMe)
			canvas.bufferAvailable(buffer);
	}
}