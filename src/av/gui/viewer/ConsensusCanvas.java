package av.gui.viewer;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import av.data.*;

class ConsensusCanvas extends JPanel
{
	private Contig contig;
	private Consensus consensus;

	private ReadsCanvas rCanvas;

	ConsensusCanvas(ReadsCanvas rCanvas)
	{
		this.rCanvas = rCanvas;

		setOpaque(false);
		setBackground(Color.red);

		setPreferredSize(new Dimension(0, 25));
	}

	void setContig(Contig contig)
	{
		this.contig = contig;
		consensus = contig.getConsensus();
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

		g.translate(-rCanvas.pX1 + 1, 0); // +1 for edge of display


		int offset = contig.getConsensusOffset();
		System.out.println("offset is " + offset);

		System.out.println("Drawing " + xS + " to " + xE);

		byte[] data = consensus.getRange(xS, xE);


		int y = 0;
		for (int i = 0, x = (ntW*xS); i < data.length; i++, x += ntW)
		{
			if (data[i] != -1)
			{
				switch (data[i])
				{
					case Sequence.P:  g.drawString("*", x+2, y+12); break;
					case Sequence.dP: g.drawString("*", x+2, y+12); break;
					case Sequence.A:  g.drawString("A", x+2, y+12); break;
					case Sequence.dA: g.drawString("A", x+2, y+12); break;
					case Sequence.T:  g.drawString("T", x+2, y+12); break;
					case Sequence.dT: g.drawString("T", x+2, y+12); break;
					case Sequence.C:  g.drawString("C", x+2, y+12); break;
					case Sequence.dC: g.drawString("C", x+2, y+12); break;
					case Sequence.G:  g.drawString("G", x+2, y+12); break;
					case Sequence.dG: g.drawString("G", x+2, y+12); break;
					case Sequence.N:  g.drawString("N", x+2, y+12); break;
					case Sequence.dN: g.drawString("N", x+2, y+12); break;
				}

				g.drawRect(x, y, ntW, ntH);
			}
		}
	}
}