package av.gui.viewer;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import av.data.*;
import av.gui.viewer.colors.*;

class ConsensusCanvas extends JPanel
{
	private Contig contig;
	private Consensus consensus;

	private ReadsCanvas rCanvas;

	// The LHS offset (difference) between the left-most read and the consensus
	int offset;

	ConsensusCanvas(ReadsCanvas rCanvas)
	{
		this.rCanvas = rCanvas;

		setOpaque(false);

		setPreferredSize(new Dimension(0, 25));
	}

	void setContig(Contig contig)
	{
		this.contig = contig;

		consensus = contig.getConsensus();
		offset = contig.getConsensusOffset();
	}

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


		System.out.println("Drawing " + xS + " to " + xE);

		byte[] data = consensus.getRange(xS-offset, xE-offset);


		int y = 0;
		for (int i = 0, x = (ntW*xS); i < data.length; i++, x += ntW)
		{
			if (data[i] != -1)
				g.drawImage(colors.getImage(data[i]), x, y, null);
		}
	}
}