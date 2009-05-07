package tablet.gui.viewer;

import java.awt.*;
import java.awt.image.*;
import java.text.*;
import javax.swing.*;

import tablet.data.*;
import tablet.gui.viewer.colors.*;

class ScaleCanvas extends JPanel
{
	private static final NumberFormat d = NumberFormat.getInstance();

	private Contig contig;
	private Consensus consensus;
	private ReadsCanvas rCanvas;

	private int h = 22;

	// The data index position (0 indexed) of the base under the mouse
	// May be negative too
	private Integer mouseBase;

	// The LHS offset (difference) between the left-most read and the consensus
	int offset;

	ScaleCanvas()
	{
		setOpaque(false);
		setPreferredSize(new Dimension(0, h));
	}

	void setAssemblyPanel(AssemblyPanel aPanel)
	{
		rCanvas = aPanel.readsCanvas;
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

	void setMouseBase(Integer mouseBase)
	{
		this.mouseBase = mouseBase;
		repaint();
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

		// Determine rhs of canvas
		int x1 = rCanvas.pX1;
		int x2 = rCanvas.pX2;
		if (x2 > rCanvas.canvasW)
			x2 = rCanvas.canvasW;

		g.setColor(new Color(167, 166, 170));
		g.drawLine(x1, 0, x1, h);
		g.drawLine(x1, 3, x2-1, 3);
		g.drawLine(x2-1, 0, x2-1, h);

		// Draw ticks at intervals of 5 and 25 bases
		for (int i = xS; i <= xE; i++)
		{
			if ((i-offset+1) % 25 == 0)
				g.drawLine(i*ntW+(ntW/2), 0, i*ntW+(ntW/2), 8);

			else if ((i-offset+1) % 5 == 0)
				g.drawLine(i*ntW+(ntW/2), 3, i*ntW+(ntW/2), 8);
		}

		g.setColor(Color.red);
		g.drawString(d.format(xS+1-offset), x1+5, 20);
		g.drawString(d.format(xE+1-offset), x2-25, 20);

		if (mouseBase != null)
		{
			String str = d.format(mouseBase+1) + " ("
				+ d.format(mouseBase+1) + ")";
			int strWidth = g.getFontMetrics().stringWidth(str);

			g.setColor(Color.red);

			int tick = (mouseBase+offset) * ntW + (ntW/2);
			g.drawLine(tick, 0, tick, 3);

			// Work out where to start drawing: base position + 1/2 a base
			int x = (mouseBase+offset) * ntW + (ntW/2);

			int pos = getPosition(x, strWidth);

			g.drawString(str, pos, 20);
		}
	}

	// Computes the best position to draw a string onscreen, assuming an optimum
	// start position that *may* be adjusted if the text ends up partially drawn
	// offscreen on either the LHS or the RHS
	private int getPosition(int pos, int strWidth)
	{
		// Work out where the left and right hand edges of the text will be
		int leftPos = pos-(int)(strWidth/2f);
		int rghtPos = pos+(int)(strWidth/2f);

		// If we're offscreen to the left, adjust...
		if (leftPos < rCanvas.pX1)
			leftPos = rCanvas.pX1+1;
		// Similarly if we're offscreen to the right...
		if (rghtPos > rCanvas.pX2)
			leftPos = rCanvas.pX2-strWidth-1;

		return leftPos;
	}
}