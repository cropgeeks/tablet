// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;

import tablet.data.auxiliary.*;

class CoverageOverviewFactory extends OverviewBufferFactory
{
	private ReadsCanvas rCanvas;

	CoverageOverviewFactory(OverviewCanvas canvas, int w, int h, ReadsCanvas rCanvas)
	{
		super(canvas, w, h);

		// Make private references to certain values now, as they MAY change
		// while the buffer is still being created, which will create problems
		this.rCanvas = rCanvas;

		start();
	}

	public void run()
	{
		setPriority(Thread.MIN_PRIORITY);
		setName("CoverageOverview-BufferFactory");

		try { Thread.sleep(500); }
		catch (InterruptedException e) {}

		if (killMe)
			return;

		int overviewWidth = (canvas.oE-canvas.oS+1);

		// Get the coverage information
		int[] coverage = DisplayData.getCoverage();
		int coverageMax = DisplayData.getMaxCoverage();

		// Work out how many bases per block to average over, and hence how many
		// blocks we can chop the data in to (always rounded UP due to pixels)
		int numPerBlock = (int) Math.ceil(coverage.length / (float) overviewWidth);
		int blocks = (int) Math.ceil(overviewWidth / (float) numPerBlock);

		// We need to store the average value per set of bases
		int[] averages = new int[blocks];
		// And also store the maximum value found for each block
		int[] maxes = new int[blocks];
		int blockMax = 0;

		// Adjustment factor to deal with overview subsetting
		int adjust = Math.abs(rCanvas.contig.getVisualStart()-canvas.oS);

		for (int i = (0 + adjust), num = 0, block = 0; i < (overviewWidth + adjust); i++)
		{
			averages[block] += coverage[i];
			num++;

			if (coverage[i] > maxes[block])
				maxes[block] = coverage[i];

			if (num == numPerBlock || i == coverage.length-1)
			{
				averages[block] /= num;

				if (averages[block] > blockMax)
					blockMax = averages[block];

				num = 0;
				block++;
			}
		}


		Graphics2D g = createBuffer();
		g.setColor(Color.darkGray);

		float xScale = averages.length / (float) w;

		for (int x = 0; x < w && !killMe; x++)
		{
			// Work out where each pixel maps to in the data
			int dataX = (int) (x * xScale);

			// Convert the value to a percentage
			float value = averages[dataX] / (float) blockMax;

			// Percentage value of highest value within the block
			float percent = maxes[dataX] / (float) coverageMax;
			// Work out an intensity value for it (0-255 gives light shades too
			// close to white, so adjust the scale to 25-255)
			int alpha = 25 + (int) (((255-25) * (255*percent)) / 255f);

			// Then draw a line of height x percentage
			g.setColor(new Color(70, 116, 162, alpha));
			g.drawLine(x, 0, x, Math.round(h*value));
		}

		if (!killMe)
			canvas.bufferAvailable(buffer);
	}
}