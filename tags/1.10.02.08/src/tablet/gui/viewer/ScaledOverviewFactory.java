// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;

import tablet.data.*;

class ScaledOverviewFactory extends OverviewBufferFactory
{
	private ReadsCanvas rCanvas;
	private IReadManager reads;

	private int ntOnCanvasX, ntOnCanvasY;

	ScaledOverviewFactory(OverviewCanvas canvas, int w, int h, ReadsCanvas rCanvas)
	{
		super(canvas, w, h);
		this.rCanvas = rCanvas;

		// Make private references to certain values now, as they MAY change
		// while the buffer is still being created, which will create problems
		reads = rCanvas.reads;
		ntOnCanvasX = rCanvas.ntOnCanvasX;
		ntOnCanvasY = rCanvas.ntOnCanvasY;

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
		float xScale = ntOnCanvasX / (float) w;
		float yScale = ntOnCanvasY / (float) h;

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
				int dataX = (int) (x * xScale) - rCanvas.offset;

				// Then drawing that data (or not)
				Read read = reads.getReadAt(dataY, dataX);
				if (read != null)
				{
					ReadMetaData rmd = Assembly.getReadMetaData(read, true);
					byte b = rmd.getStateAt(dataX-read.getStartPosition());

					g.setColor(rCanvas.colors.getColor(b));
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

/* Code that might be useful for "data coverage" overview. Possibly superceded
 * by the proper coverage overview histogram.

// Loop over every pixel that makes up the overview...
for (int y = 0; y < h && !killMe; y++)
{
	int dataY = (int) (y * yScale);

	for (int x = 0; x < w && !killMe; x++)
	{
		// Working out where each pixel maps to in the data...
		// Each overview pixel maps to a window of data (x1 to x2)
		int dataX1 = (int) (x * xScale) - rCanvas.offset;
		int dataX2 = dataX1 + (int) xScale;

		// Determine the percentage of actual data in this window
		byte[] data = reads.getValues(dataY, dataX1, dataX2);

		float dataCount = 0;
		for (int i = 0; i < data.length; i++)
			if (data[i] != -1)
				dataCount++;

		float percent = dataCount / (float)data.length;

		// Draw the lower 10% in red
		if (percent > 0 && percent < 0.1f)
		{
			g.setColor(new Color(255, 0, 0, 100));
			g.drawLine(x, y, x, y);
		}
		// And the rest in shades of blue
		else if (percent > 0)
		{
			g.setColor(new Color(0, 0, 255, (int)(255*(percent))));
			g.drawLine(x, y, x, y);
		}
	}
}

*/