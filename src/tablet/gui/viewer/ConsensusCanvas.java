package tablet.gui.viewer;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import tablet.data.*;
import tablet.gui.viewer.colors.*;

class ConsensusCanvas extends JPanel
{
	private Contig contig;
	private Consensus consensus;
	private ReadsCanvas rCanvas;

	// The LHS offset (difference) between the left-most read and the consensus
	int offset;

	// Low/high colour information used to draw the base quality scores
	private int[] c1;
	private int[] c2;

	private Dimension dimension = new Dimension();

	ConsensusCanvas(ReadsCanvas rCanvas)
	{
		this.rCanvas = rCanvas;

		setOpaque(false);

		// Set up the base quality colours
		Color col1 = new Color(255, 120, 120); // low
		Color col2 = new Color(120, 255, 120); // high

		c1 = new int[] { col1.getRed(), col1.getGreen(), col1.getBlue() };
		c2 = new int[] { col2.getRed(), col2.getGreen(), col2.getBlue() };
	}

	void setContig(Contig contig)
	{
		this.contig = contig;

		if (contig != null)
		{
			consensus = contig.getConsensus();
			offset = contig.getConsensusOffset();
		}
	}

	void setDimensions()
	{
		dimension = new Dimension(0, rCanvas.ntH/2 + 2 + rCanvas.ntH +2);

		setPreferredSize(dimension);
		revalidate();
	}

	public Dimension getPreferredSize()
		{ return dimension; }

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;

		if (contig == null)
			return;

		int ntW = rCanvas.ntW;
		int ntH = rCanvas.ntH;
		int xS = rCanvas.xS;
		int xE = rCanvas.xE;

		ColorScheme colors = rCanvas.colors;

		g.translate(-rCanvas.pX1 + 1, 0); // +1 for edge of display


		// Draw the quality scores
		byte[] bq = consensus.getBaseQualityRange(xS-offset, xE-offset);
		int y = 0;

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

				g.fillRect(x, y, rCanvas.ntW, rCanvas.ntH/2);
			}
		}



		byte[] data = consensus.getRange(xS-offset, xE-offset);
		y = rCanvas.ntH/2 + 2;

		// Draw the consensus sequence
		for (int i = 0, x = (ntW*xS); i < data.length; i++, x += ntW)
			if (data[i] != -1)
				g.drawImage(colors.getImage(data[i]), x, y, null);
	}
}