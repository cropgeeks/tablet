package tablet.gui.viewer;

import java.awt.*;

import tablet.analysis.*;
import tablet.data.*;

class CoverageOverviewFactory extends OverviewBufferFactory
{
	private Contig contig;

	CoverageOverviewFactory(OverviewCanvas canvas, int w, int h, ReadsCanvas rCanvas)
	{
		super(canvas, w, h);

		// Make private references to certain values now, as they MAY change
		// while the buffer is still being created, which will create problems
		contig = rCanvas.contig;

		start();
	}

	public void run()
	{
		setPriority(Thread.MIN_PRIORITY);
		setName("CoverageOverview-BufferFactory");

		try { Thread.sleep(250); }
		catch (InterruptedException e) {}

		if (killMe)
			return;

		// Get the coverage information
		CoverageCalculator cc = new CoverageCalculator(contig);
		int[] coverage = cc.getCoverage();
		int coverageMax = cc.getMaximum();

		// Work out how many bases per block to average over, and hence how many
		// blocks we can chop the data in to (always rounded UP due to pixels)
		int numPerBlock = (int) Math.ceil(coverage.length / (float) w);
		int blocks = (int) Math.ceil(coverage.length / (float) numPerBlock);

		// We need to store the average value per set of bases
		int[] averages = new int[blocks];
		// And also store the maximum value found for each block
		int[] maxes = new int[blocks];
		int blockMax = 0;

		for (int i = 0, num = 0, block = 0; i < coverage.length; i++)
		{
			averages[block] += coverage[i];
			num++;

			if (coverage[i] > maxes[block])
				maxes[block] = coverage[i];

			if (num == numPerBlock || i == coverage.length-1)
			{
				averages[block] /= num;

				if (i == coverage.length-1)
					System.out.println("final block contained " + num + " values");

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

		// Remove any references to tablet.data objects we were tracking
		contig = null;

		if (!killMe)
			canvas.bufferAvailable(buffer);
	}
}