// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

import tablet.gui.*;

public class OverviewCanvas extends JPanel
{
	public static final int SCALEDDATA = 0;
	public static final int COVERAGE = 1;

	private AssemblyPanel aPanel;
	private ReadsCanvas rCanvas;

	private Canvas2D canvas = new Canvas2D();
	private OverviewCanvasML canvasML;

	private OverviewBufferFactory bufferFactory = null;
	private BufferedImage image = null;
	private int w, h;

	// Scaling factors from main canvas back to the overview
	private float xScale, yScale;
	// Outline box co-ordinates
	private int bX1, bY1, bX2, bY2;
	// Read under mouse tracking co-ordinates
	private int readX = -1, readY, readW, readH;

	// Animation timer and an alpha value used when the buffer is ready
	private Timer timer;
	private float alpha;

	int visualS;
	int visualE;
	int l, r;
	boolean dragging = false;

	OverviewCanvas()
	{
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(0, 75));
		add(canvas);

		setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createEmptyBorder(2, 0, 5, 0),
			BorderFactory.createLineBorder(new Color(167, 166, 170))));

		// Resize listener (resized = time to redraw)
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				createImage();
			}
		});

		canvasML = new OverviewCanvasML(this, canvas);
		createTimer();
	}

	void displayMenu(JComponent button, MouseEvent e)
		{ canvasML.displayMenu(button, e); }

	void processMouse(MouseEvent e)
		{ canvas.processMouse(e); }

	void setAssemblyPanel(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;
		rCanvas = aPanel.readsCanvas;
	}

	void createTimer()
	{
		timer = new Timer(50, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// We want the alpha to go opaque to transparent in 10 steps
				alpha -= 0.1;

				if (alpha <= 0)
					timer.stop();

				repaint();
			}
		});

		timer.setInitialDelay(0);
	}

	void createImage()
	{
		w = canvas.getSize().width;
		h = canvas.getSize().height;

		if (w == 0 || h == 0 || rCanvas.contig == null)
			return;

		image = null;

		// Kill off any old image generation that might still be running...
		if (bufferFactory != null)
		{
			bufferFactory.killMe = true;
			bufferFactory.interrupt();
		}

		// Before starting a new one
		if (Prefs.visOverviewType == SCALEDDATA)
			bufferFactory = new ScaledOverviewFactory(this, w>0 ? w:1, h>0 ? h:1, rCanvas);
		else if (Prefs.visOverviewType == COVERAGE)
			bufferFactory = new CoverageOverviewFactory(this, w>0 ? w:1, h>0 ? h:1, rCanvas);

		repaint();
	}

	void bufferAvailable(BufferedImage image)
	{
		this.image = image;

		// Scaling factors for mouse/mapping
		xScale = w / (float) rCanvas.ntOnCanvasX;
		yScale = h / (float) rCanvas.ntOnCanvasY;

		// Force the main canvas to send its view size dimensions so we can draw
		// the highlighting box on top of the new back buffer's image
		rCanvas.updateOverview();

		alpha = 1.0f;
		timer.restart();
	}

	void updateOverview(int xIndex, int xNum, int yIndex, int yNum)
	{
		if (bufferFactory == null)
			return;

		// Work out the x1 position for the outline box
		bX1 = Math.round(xScale * xIndex);
		if (bX1 >= w) bX1 = w - 1;

		// Work out the y1 position for the outline box
		bY1 = Math.round(yScale * yIndex);
		if (bY1 >= h) bY1 = h - 1;

		// Work out the x2 position for the outline box
		bX2 = bX1 + Math.round(xNum * xScale);
		if (bX2 >= w) bX2 = w - 1;

		// Work out the y2 position for the outline box
		bY2 = bY1 + Math.round(yNum * yScale);
		if (bY2 >= h) bY2 = h - 1;

		// Tweak for a fatter outline on very large canvases
		if (bX1 == bX2) bX2++;
		if (bY1 == bY2) bY2++;

		repaint();
	}

	// TODO: These don't always map very well to the overview's data, probably
	// because we draw the data pixel by pixel, but do the outlines by trying to
	// map from data-indices back to pixels
	void updateRead(int lineIndex, int start, int end)
	{
		if (Prefs.visOverviewType == SCALEDDATA)
		{
			readX = Math.round(xScale * start);
			readY = Math.round(yScale * lineIndex);
			readW = Math.round(xScale * (end-start+1));
			readH = Math.round(yScale);

			if (readH < 1) readH = 1;

			repaint();
		}
	}

	/**
	 * Once the user has ceased control dragging, we need to work out the final
	 * positions of the subset variables, then set this information on the contig.
	 *
	 * @param e
	 */
	void setSubset(MouseEvent e)
	{
		int visS, visE;
		visualE = e.getX();

		if(visualS < visualE)
		{
			visS = (visualS - 0) * (aPanel.getContig().getVisualE() - aPanel.getContig().getVisualS()) / (w-0) + aPanel.getContig().getVisualS();
			visE = (visualE - 0) * (aPanel.getContig().getVisualE() - aPanel.getContig().getVisualS()) / (w-0) + aPanel.getContig().getVisualS();
		}
		else
		{
			visS = (visualE - 0) * (aPanel.getContig().getVisualE() - aPanel.getContig().getVisualS()) / (w-0) + aPanel.getContig().getVisualS();
			visE = (visualS - 0) * (aPanel.getContig().getVisualE() - aPanel.getContig().getVisualS()) / (w-0) + aPanel.getContig().getVisualS();
		}
		aPanel.getContig().setVisualS(visS);
		aPanel.getContig().setVisualE(visE);
		aPanel.setContig(aPanel.getContig());

		dragging = false;
		repaint();
	}

	/**
	 * Updates the variables for drawing the black rectangles on the overview
	 * display.
	 *
	 * @param e
	 */
	void drawSubset(MouseEvent e)
	{
		visualE = e.getX();

		if(visualS < visualE)
		{
			l = visualS;
			r = visualE;
		}
		else
		{
			l = visualE;
			r = visualS;
		}
		repaint();
	}

	public AssemblyPanel getAssemblyPanel()
	{
		return aPanel;
	}

	private class Canvas2D extends JPanel
	{
		Canvas2D()
		{
			setBackground(Color.white);
		}

		private void processMouse(MouseEvent e)
		{
			if(!e.isControlDown() && !e.isMetaDown())
			{
				// Compute mouse position (and adjust by wid/hgt of rectangle)
				int x = e.getX() - (int) ((bX2-bX1+1) / 2f);
				int y = e.getY() - (int) ((bY2-bY1+1) / 2f);

				int rowIndex = (int) (y / yScale);
				int colIndex = (int) (x / xScale);

				aPanel.moveToPosition(rowIndex, colIndex, false);
			}
		}

		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			Graphics2D g = (Graphics2D) graphics;

			if (image == null)
				return;

			// Paint the image of the alignment
			g.drawImage(image, 0, 0, null);

			// Then draw the tracking rectangle
			g.setPaint(new Color(0, 0, 255, 50));
			g.fillRect(bX1, bY1, bX2-bX1, bY2-bY1);
			g.setColor(Color.red);
			g.drawRect(bX1, bY1, bX2-bX1, bY2-bY1);

			if (readX >= 0)
			{
				g.setColor(Color.blue);
				g.drawRect(readX, readY, readW-1, readH-1);
			}

			// White overlay that gives the fade-in effect
			if (alpha >= 0)
			{
				g.setColor(new Color(1f, 1f, 1f, alpha));
				g.fillRect(0, 0, w, h);
			}

			paintSubsetOverlays(g);
		}
	}

	/**
	 * Carries out the painting for all subset infromation which is to be
	 * overlayed on top of the overview canvas.
	 *
	 * @param g
	 */
	void paintSubsetOverlays(Graphics2D g)
	{
		if(dragging)
		{
			g.setPaint(new Color(0, 0, 0, 200));
			g.fillRect(0, 0, l, h);
			g.fillRect(r, 0, w-r, h);
		}
//
//		if(aPanel.rulerCanvas.dragging || aPanel.rulerCanvas.resizeLeft || aPanel.rulerCanvas.resizeRight)
//		{
//			g.setPaint(new Color(255, 255, 255, 200));
//			g.fillRect(0, 0, w, h);
//			g.setFont(new Font("Monospaced", Font.BOLD, 18));
//		}
//		else
//			g.setFont(new Font("Monospaced", Font.PLAIN, 12));
//
//		g.setColor(Color.black);
//		g.drawString(""+(aPanel.rulerCanvas.getVisualS()+1), 5, h-5);
//
//		String rhsStr = ""+(aPanel.rulerCanvas.getVisualE()+1);
//		int pos = getPosition(w-5, g.getFontMetrics().stringWidth(rhsStr));
//		g.drawString(rhsStr, pos, h-5);
	}

	// Computes the best position to draw a string onscreen, assuming an optimum
	// start position that *may* be adjusted if the text ends up partially drawn
	// offscreen on either the LHS or the RHS
	private int getPosition(int pos, int strWidth)
	{
		// Work out where the left and right hand edges of the text will be
		int leftPos = pos-(int)(strWidth/2f);
		int rghtPos = pos+(int)(strWidth/2f);

		// Similarly if we're offscreen to the right...
		if (rghtPos > w)
			leftPos = w-strWidth-3;

		return leftPos;
	}
}

/**
 * Abstract base class for all overview image-generation factories.
 */
abstract class OverviewBufferFactory extends Thread
{
	protected OverviewCanvas canvas;
	protected BufferedImage buffer;
	protected int w, h;

	boolean killMe = false;

	protected OverviewBufferFactory(OverviewCanvas canvas, int w, int h)
	{
		this.canvas = canvas;
		this.w = w;
		this.h = h;
	}

	// Creates and (fills with white) an image buffer of the correct size
	protected Graphics2D createBuffer()
	{
		buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = buffer.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, w, h);

		return g;
	}
}