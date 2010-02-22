// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.data.*;

class BamBamBar extends JPanel
{
	private AssemblyPanel aPanel;
	private BamBam bambam;
	private Contig contig;

	private Canvas2D canvas = new Canvas2D();
	private int h = 13;

	// Data/visual start/end values (updated on every repaint)
	private int dS, dE, vS, vE;
	private float xScale;

	// The ghost bar
	private Integer gVS, gVE;

	private boolean isDragging = false;
	private boolean isNewContig = true;

	BamBamBar()
	{
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(0, h+3));
		setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));

		add(canvas);

		MouseHandler mh = new MouseHandler();
		canvas.addMouseListener(mh);
		canvas.addMouseMotionListener(mh);
	}

	void setAssemblyPanel(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;
	}

	void setContig(Contig contig)
	{
		this.contig = contig;

		if (contig != null)
		{
			bambam = aPanel.getAssembly().getBamBam();
			setVisible(bambam != null);

			gVS = gVE = null;
			isNewContig = true;
		}
	}

	private void trackVariables()
	{
		dS = contig.getDataStart();
		dE = contig.getDataEnd();

		// If we're not dragging with the mouse, then make sure the visual
		// variables are always up to date
		if (isDragging == false)
		{
			int cVS = contig.getVisualStart();
			int cVE = contig.getVisualEnd();

			// If they've changed, update the bar (and the ghost bar)
			if (vS != cVS)
			{
				if (isNewContig == false)
					gVS = vS;
				vS = cVS;
			}
			if (vE != cVE)
			{
				if (isNewContig == false)
					gVE = vE;
				vE = cVE;
			}
		}

		isNewContig = false;
	}

	private class Canvas2D extends JPanel
	{
		// Blues
		Color b1 = new Color(212, 233, 244);
		Color b2 = new Color(171, 192, 211).darker();

		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			Graphics2D g = (Graphics2D) graphics;

			trackVariables();

			// Width of the data
			int dW = dE - dS + 1;

			// Scaling factor (component to data)
			int w = getWidth();
			xScale = w / (float) dW;

			// Background
			g.setPaint(new GradientPaint(0, 0, b2, 0, h/2f, b1, true));
			g.fillRect(0, 1, w, h-2);
			g.setColor(new Color(130, 130, 135));
			g.drawRect(0, 1, w-1, h-3);
			g.setColor(new Color(35, 76, 101));

			// Draw the ghost bar (if needed)
			if (gVS != null)
				drawBar(g, gVS, gVE, 100);

			// Then the main bar
			drawBar(g, vS, vE, 255);
		}

		private void drawBar(Graphics2D g, int vS, int vE, int alpha)
		{
			int w = getWidth();

			// Width of the visual block
			int vW = vE - vS + 1;

			// Visual start (in x coordinates)
			int xVS = (int) ((vS-dS) * xScale);
			// Visual width (in x coordinates)
			int xVW = (int) (vW * xScale);

			// Now adjust to ensure the bar always fits nicely on screen
			// Make it a minimum of 5 pixels wide, not wider than the screen,
			// and not beyond the right-hand side of the screen
			if (xVW < 9)
				xVW = 9;
			else if (xVW >= w)
				xVW = w-1;
			if (xVS + xVW >= w-1)
				xVS = w-1 - xVW;

			Color w1 = new Color(255, 255, 255, alpha);
			Color w2 = new Color(190, 190, 190, alpha);
			Color outline = new Color(130, 130, 135, alpha);

			g.setPaint(new GradientPaint(0, 0, w2, 0, h/2f, w1, true));
			g.fillRect(xVS, 0, xVW, h-1);
			g.setColor(outline);
			g.drawRect(xVS, 0, xVW, h-1);
		}
	}

	private class MouseHandler extends MouseInputAdapter
	{
		// Width of a visual block (updated on mouseDown)
		private int vW;

		public void mousePressed(MouseEvent e)
		{
			// Update the ghost bar
			gVS = vS;
			gVE = vE;

			// Track what the current width is
			vW = vE - vS + 1;

			isDragging = true;
			mouseDragged(e);
		}

		public void mouseReleased(MouseEvent e)
		{
			// Update contig
			aPanel.processBamDataChange();
			aPanel.moveToPosition(-1, vS, false);

			isDragging = false;
			repaint();
		}

		public void mouseDragged(MouseEvent e)
		{
			// Visual start must be the mouse position minus half a block width
			vS = (int) (e.getPoint().x / xScale) - (vW / 2);

			// Tell the bambam object where we want to be...
			bambam.setBlockStart(contig, vS);
			// ...which will check the values are acceptable
			vS = bambam.getS();
			vE = bambam.getE();

			System.out.println(vS + " to " + vE);

			repaint();
		}
	}
}