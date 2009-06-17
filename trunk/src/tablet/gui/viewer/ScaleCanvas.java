package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;
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

		// Simple mouse listeners to update the base position on mouse overs
		addMouseListener(new MouseAdapter()
		{
			public void mouseExited(MouseEvent e)
			{
				setMouseBase(null);
			}
		});

		addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseMoved(MouseEvent e)
			{
				if (rCanvas.contig == null)
					return;

				int xIndex = (e.getX() / rCanvas.ntW) - rCanvas.offset;
				setMouseBase(xIndex);
			}
		});
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
		if (this.mouseBase != mouseBase)
		{
			this.mouseBase = mouseBase;
			repaint();
		}
	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;

		if (contig == null)
			return;

		// Determine lhs and rhs of canvas
		int x1 = rCanvas.pX1;
		int x2 = rCanvas.pX2;
		int width = (x2-x1+1);

		// Clip to only draw what's needed (mainly ignoring what would appear
		// above the vertical scrollbar of the reads canvas)
		g.setClip(1, 0, width, h);
		g.translate(1-x1, 0);


		int ntW = rCanvas.ntW;
		int ntH = rCanvas.ntH;
		int xS = x1 / ntW;
		int xE = x2 / ntW;

		// Draw the scale bar
		g.setColor(new Color(167, 166, 170));
		g.drawLine(x1, 0, x1, h);
		g.drawLine(x1, 3, x2, 3);
		g.drawLine(x2, 0, x2, h);

		// And ticks at intervals of 5 and 25 bases
		for (int i = xS; i <= xE; i++)
		{
			if ((i-offset+1) % 25 == 0)
				g.drawLine(i*ntW+(ntW/2), 0, i*ntW+(ntW/2), 8);

			else if ((i-offset+1) % 5 == 0)
				g.drawLine(i*ntW+(ntW/2), 3, i*ntW+(ntW/2), 8);
		}

		g.setColor(Color.red);

		int mouseBaseS = 0, mouseBaseE =  0;

		// REMINDER: mouseBase = base pos under mouse (0 indexed on consensus)
		if (mouseBase != null)
		{
			// If the mouse is beyond the edge of the data, don't do anything
			if (mouseBase+offset+1 > rCanvas.ntOnCanvasX)
				return;

			// Work out where to start drawing: base position + 1/2 a base
			int x = (mouseBase+offset) * ntW + (ntW/2);
			// Draw a tick there
			g.drawLine(x, 0, x, 8);

			// Then format, centre and draw the message
			String unpadded = getUnpadded(mouseBase);
			String str = d.format(mouseBase+1) + unpadded;

			int strWidth = g.getFontMetrics().stringWidth(str);
			int pos = getPosition(x, strWidth);
			g.drawString(str, pos, 20);

			mouseBaseS = pos;
			mouseBaseE = pos+strWidth-1;
		}

		// Attempt to mark the base position on the LHS of the canvas
		String lhsStr = d.format(xS+1-offset) + getUnpadded(xS-offset);
		int strWidth  = g.getFontMetrics().stringWidth(lhsStr);
		int pos = getPosition(x1, strWidth);;

		if (mouseBase == null || mouseBaseS > pos+strWidth-1)
			g.drawString(lhsStr, pos, 20);

		// Attempt to mark the base position on the RHS of the canvas
		String rhsStr = d.format(xE+1-offset) + getUnpadded(xE-offset);
		strWidth  = g.getFontMetrics().stringWidth(rhsStr);
		pos = getPosition(x2, strWidth);

		if (mouseBase == null || mouseBaseE < pos)
			g.drawString(rhsStr, pos, 20);
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
			leftPos = rCanvas.pX1+3;
		// Similarly if we're offscreen to the right...
		if (rghtPos > rCanvas.pX2)
			leftPos = rCanvas.pX2-strWidth-3;

		return leftPos;
	}

	String getUnpadded(int mouseBase)
	{
		// If the mouse is off the consensus, ignore unpadded positions
		if (mouseBase < 0 || mouseBase >= consensus.length())
			return "";

		int unpadded = consensus.getUnpaddedPosition(mouseBase);

		if (unpadded == -1)
			return " (*)";
		else
			return " (" + d.format(unpadded+1) + ")";
	}
}