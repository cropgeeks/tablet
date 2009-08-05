package tablet.gui.viewer;

import java.awt.*;
import javax.swing.*;

import tablet.data.*;
import tablet.data.auxiliary.*;

class CoverageCanvas extends TrackingCanvas
{
	private Contig contig;

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
	}

	void setContig(Contig contig)
	{
		this.contig = contig;

		if (contig != null)
			offset = contig.getConsensusOffset();
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
		float percent = DisplayData.getAverageCoverage() / (float) maxCoverage;
		int avgY = (int) (percent * h);
		g.setColor(new Color(45, 100, 162));
		g.setStroke(dashed);
		g.drawLine(x1, avgY, x2, avgY);
	}
}