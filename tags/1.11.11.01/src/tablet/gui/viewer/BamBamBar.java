// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.data.*;
import tablet.gui.*;
import tablet.gui.dialog.prefs.*;
import tablet.gui.ribbon.*;

import scri.commons.gui.*;

class BamBamBar extends JPanel implements IOverlayRenderer
{
	private static DecimalFormat d = new DecimalFormat("0.000");

	private AssemblyPanel aPanel;
	private ReadsCanvas rCanvas;
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
		rCanvas = aPanel.readsCanvas;
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

	private void updateOverlay()
	{
		if (isDragging)
			rCanvas.overlays.addLast(this);
		else
			rCanvas.overlays.remove(this);
	}

	public void render(Graphics2D g)
	{
		FontMetrics fm = g.getFontMetrics();

		// Fade out the main canvas so the text can be seen clearly
		g.setPaint(new Color(255, 255, 255, 200));
		g.fillRect(0, 0, rCanvas.getWidth(), rCanvas.getHeight());

		String label = TabletUtils.nf.format(vS+1) + " - "
			+ TabletUtils.nf.format(vE+1);
		int width = fm.stringWidth(label);
		int x = rCanvas.pX1 + ((rCanvas.pX2Max-rCanvas.pX1)/2) - (width/2);
		g.setColor(Color.red);
		g.drawString(label, x, rCanvas.pY1+50);

		label = RB.format("gui.viewer.BamBamBar.label2",
			TabletUtils.nf.format(vE-vS+1),
			d.format((vE-vS+1)/(float)contig.getDataWidth()*100));
		width = fm.stringWidth(label);
		x = rCanvas.pX1 + ((rCanvas.pX2Max-rCanvas.pX1)/2) - (width/2);
		g.setColor(Color.gray);
		g.drawString(label, x, rCanvas.pY1+80);

		String ctrl = SystemUtils.isMacOS() ? RB.getString("gui.text.cmnd") :
			RB.getString("gui.text.ctrl");
		label = RB.format("gui.viewer.BamBamBar.label3", ctrl);
		width = fm.stringWidth(label);
		x = rCanvas.pX1 + ((rCanvas.pX2Max-rCanvas.pX1)/2) - (width/2);
		g.drawString(label, x, rCanvas.pY1+100);
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

		canvas.setToolTipText(RB.format("gui.viewer.BamBamBar.tooltip",
			vS+1, vE+1));
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

	void bamPrevious()
	{
		bambam.setBlockStart(contig, gVS);
		aPanel.processBamDataChange();
		aPanel.moveToPosition(-1, gVS, false);
	}

	private class MouseHandler extends MouseInputAdapter implements ActionListener
	{
		private JMenuItem mOptions;
		private JMenuItem mGhost;

		// Width of a visual block (updated on mouseDown)
		private int vW;

		public void mousePressed(MouseEvent e)
		{
			if (e.isPopupTrigger())
				displayMenu(e);

			else if (SwingUtilities.isLeftMouseButton(e))
			{
				// Update the ghost bar
				gVS = vS;
				gVE = vE;

				// Track what the current width is
				vW = vE - vS + 1;

				isDragging = true;
				updateOverlay();

				mouseDragged(e);
			}
		}

		public void mouseReleased(MouseEvent e)
		{
			if (e.isPopupTrigger())
				displayMenu(e);

			else if (isDragging)
			{
				// Update contig
				if (vS != gVS || vE != gVE)
				{
					try
					{
						aPanel.processBamDataChange();
						aPanel.moveToPosition(-1, vS, false);
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}

				isDragging = false;
				updateOverlay();

				rCanvas.repaint();
				repaint();
			}
		}

		public void mouseDragged(MouseEvent e)
		{
			if (isDragging == false)
				return;

			// Visual start must be the mouse position minus half a block width
			vS = (int) (e.getPoint().x / xScale) - (vW / 2);

			// Tell the bambam object where we want to be...
			bambam.setBlockStart(contig, vS);
			// ...which will check the values are acceptable
			vS = bambam.getS();
			vE = bambam.getE();

			rCanvas.repaint();
			repaint();
		}

		private void displayMenu(MouseEvent e)
		{
			JPopupMenu menu = new JPopupMenu();

			mGhost = new JMenuItem("", Icons.getIcon("BAMPREVIOUS16"));
			RB.setText(mGhost, "gui.viewer.BamBamBar.mGhost");
			mGhost.addActionListener(this);
			mGhost.setEnabled(gVS != null);
			menu.add(mGhost);
			menu.addSeparator();

			mOptions = new JMenuItem("", Icons.getIcon("BAMWINDOW16"));
			RB.setText(mOptions, "gui.viewer.BamBamBar.mOptions");
			mOptions.addActionListener(this);
			menu.add(mOptions);

			menu.show(e.getComponent(), e.getX(), e.getY());
		}

		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == mOptions)
				BandBAM.bWindow.doActionClick();

			// Jump the display back to the position of the ghost bar
			else if (e.getSource() == mGhost)
				bamPrevious();
		}
	}
}