// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.text.*;
import javax.swing.*;

import scri.commons.gui.*;

import tablet.data.auxiliary.*;
import tablet.gui.*;
import tablet.gui.viewer.colors.*;

public class OverviewCanvas extends JPanel
{
	public static final int SCALEDDATA = 0;
	public static final int COVERAGE = 1;

	private AssemblyPanel aPanel;
	private static ReadsCanvas rCanvas;

	private Canvas2D canvas = new Canvas2D();
	private OverviewCanvasML canvasML;

	private OverviewBufferFactory bufferFactory = null;
	private BufferedImage image = null;
	private int w, h, l, r;
	// Subsetting variables
	static int tempOS, tempOE, oS, oE;

	// Scaling factors from main canvas back to the overview
	private float xScale, yScale;
	// Outline box co-ordinates
	private int bX1, bY1, bX2, bY2;
	// Read under mouse tracking co-ordinates
	private int readX = -1, readY, readW, readH;

	// Animation timer and an alpha value used when the buffer is ready
	private Timer timer;
	private float alpha;

	private String overviewCoordinates;

	private DecimalFormat dc = new DecimalFormat("#.#");

	boolean dragging = false;
	boolean isOSX = SystemUtils.isMacOS();

	OverviewCanvas()
	{
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(0, 75));
		add(canvas);

		setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createEmptyBorder(0, 0, 5, 0),
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

	public void createImage()
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
		{
			bufferFactory = new ScaledOverviewFactory(this, w, h, rCanvas);

			canvas.setToolTipText(null);
		}
		else if (Prefs.visOverviewType == COVERAGE)
		{
			bufferFactory = new CoverageOverviewFactory(this, w, h, rCanvas);

			canvas.setToolTipText(
				RB.format("gui.viewer.OverviewCanvas.coverageTT",
				TabletUtils.nf.format(DisplayData.getAveragePercentage()),
				TabletUtils.nf.format(DisplayData.getAverageCoverage()),
				TabletUtils.nf.format(DisplayData.getMaxCoverage())));
		}

		overviewCoordinates =  TabletUtils.nf.format(oS+1) + " to " +  TabletUtils.nf.format(oE+1) + getBasePairString((oE-oS)+1);
		repaint();
	}

	void bufferAvailable(BufferedImage image)
	{
		this.image = image;

		// Scaling factors for mouse/mapping
		xScale = w / (float) (oE-oS+1);
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
		if (bX1 >= w && !isSubsetted())
			bX1 = w - 1;

		// Work out the y1 position for the outline box
		bY1 = Math.round(yScale * yIndex);
		if (bY1 >= h) bY1 = h - 1;

		// Work out the x2 position for the outline box
		bX2 = bX1 + Math.round(xNum * xScale);
		if (bX2 >= w && !isSubsetted())
			bX2 = w - 1;

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

	private String getBasePairString(int width)
	{
		String basePairs = "";

		if(width < 1000)
			basePairs += " (" + width + " bp";
		else if(width < 1000000)
			basePairs += " (" + dc.format(width / (float) 1000) + " Kb";
		else if(width < 1000000000)
			basePairs += " (" + dc.format(width / (float) 1000000) + " Mbp";
		else
			basePairs += " (" + dc.format(width / (float) 1000000000) + " Gbp";

		if(oS != rCanvas.contig.getVisualStart() || oE != rCanvas.contig.getVisualEnd())
			basePairs += ", Subsetted)";
		else
			basePairs += ")";

		return basePairs;
	}

	/**
	 * Map one range of values onto another. x is the value being mapped, with
	 * inMin and inMax being the minimum and maximum values in x's range. outMin
	 * and outMax are the minimum and maximum values of the range the value is
	 * being scaled on to.
	 */
	int map(int x, int inMin, int inMax, int outMin, int outMax)
	{
		int inSpan = inMax - inMin;
		int outSpan = outMax - outMin;

		float valueScaled = (x - inMin) / (float) inSpan;

		return (int) (outMin + (valueScaled * outSpan));
	}

	/**
	 * Called to update the variables used to draw the shaded out sections when
	 * meta-dragging to select a subset overview area.
	 */
	void updateSubsetVariables(MouseEvent e)
	{
		tempOE = e.getX();

		if(tempOS < tempOE)
		{
			l = tempOS;
			r = tempOE;
		}
		else
		{
			l = tempOE;
			r = tempOS;
		}
		repaint();
	}

	/**
	 * Actually set the subset on mouse release. This sets the oS and oE
	 * variables and calls the method to create a new overview image.
	 */
	void setSubset(MouseEvent e)
	{
		tempOE = e.getX();

		if(tempOE > w)
			tempOE = w;
		else if(tempOE < 0)
			tempOE = 0;

		if(tempOS < tempOE)
		{
			oS = map(tempOS, 0, w, oS, oE);
			oE = map(tempOE, 0, w, oS, oE);
		}
		else
		{
			oS = map(tempOE, 0, w, oS, oE);
			oE = map(tempOS, 0, w, oS, oE);
		}
		createImage();

		Actions.overviewReset.setEnabled(true);
	}

	public void setSubset(int oS, int oE)
	{
		this.oS = oS;
		this.oE = oE;

		createImage();

		Actions.overviewReset.setEnabled(true);
	}

	public void resetOverview()
	{
		oS = aPanel.getContig().getVisualStart();
		oE = aPanel.getContig().getVisualEnd();
		createImage();

		Actions.overviewReset.setEnabled(false);
	}

	public static boolean isSubsetted()
	{
		return oS != rCanvas.contig.getVisualStart() || oE != rCanvas.contig.getVisualEnd();
	}

	public int getOS()
		{	return oS;	}

	public int getOE()
		{	return oE;	}


	private class Canvas2D extends JPanel
	{
		Canvas2D()
		{
			setBackground(Color.white);
		}

		private boolean isMetaClick(MouseEvent e)
		{
			return isOSX && e.isMetaDown() || !isOSX && e.isControlDown();
		}

		private void processMouse(MouseEvent e)
		{
			if(!isMetaClick(e))
			{
				// Compute mouse position (and adjust by wid/hgt of rectangle)
				int x = e.getX() - (int) ((bX2-bX1+1) / 2f);
				int y = e.getY() - (int) ((bY2-bY1+1) / 2f);

				int rowIndex = (int) (y / yScale);
				int colIndex = (int) (x / xScale);

				colIndex += Math.abs(rCanvas.contig.getVisualStart()-oS);

				aPanel.getController().moveTo(rowIndex, colIndex, false);
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

			// Draw the base information text
			paintCoordinatesText(g);

			// Then draw the tracking rectangle
			g.setPaint(new Color(0, 0, 255, 50));

			// The scaled adjust figure required when the overview has been subsetted
			int scaledAdjust = Math.round(Math.abs(rCanvas.contig.getVisualStart()-oS)*xScale);
			int bXL = bX1 - scaledAdjust;
			int bXR = bX2 - scaledAdjust;

			g.fillRect(bXL, bY1, bXR-bXL, bY2-bY1);
			g.setColor(ColorPrefs.get("User.Overview.Outline"));
			g.drawRect(bXL, bY1, bXR-bXL, bY2-bY1);

			if (readX >= 0)
			{
				g.setColor(ColorPrefs.get("User.Overview.ReadHighlight"));
				g.drawRect(readX-scaledAdjust, readY, readW-1, readH-1);
			}

			// White overlay that gives the fade-in effect
			if (alpha >= 0)
			{
				g.setColor(new Color(1f, 1f, 1f, alpha));
				g.fillRect(0, 0, w, h);
			}

			paintSubsetOverlays(g);
		}

		private void paintCoordinatesText(Graphics2D g)
		{
			if (!Prefs.guiHideOverviewPositions)
			{
				// lhs
				g.setPaint(new Color(255, 255, 255, 130));
				FontMetrics fm = g.getFontMetrics();
				g.fillRect(0, h - 6 - fm.getHeight(), fm.stringWidth(overviewCoordinates) + 10, fm.getHeight() + 6);
				g.setColor(Color.BLACK);
				g.drawString(overviewCoordinates, 5, h - 6);
				// rhs
				int rCanvasL = rCanvas.getBaseForPixel(rCanvas.pX1) + 1;
				// Get the value of the base one past the final pixel. Need to
				// adjust back down at end of dataset / loaded block.
				int rCanvasR = rCanvas.getBaseForPixel(rCanvas.pX2+1) + 1;
				if (rCanvasR > rCanvas.contig.getVisualEnd())
					rCanvasR = rCanvas.contig.getVisualEnd() + 1;

				String rCanvasCoordinates = "" + TabletUtils.nf.format(rCanvasL) + " to " + TabletUtils.nf.format(rCanvasR) + getBasePairString(rCanvasR - rCanvasL + 1);
				g.setPaint(new Color(255, 255, 255, 130));
				g.fillRect(w - (fm.stringWidth(rCanvasCoordinates) + 10), h - 6 - fm.getHeight(), fm.stringWidth(rCanvasCoordinates) + 10, fm.getHeight() + 6);
				g.setColor(Color.red);
				g.drawString(rCanvasCoordinates, w - (fm.stringWidth(rCanvasCoordinates) + 5), h - 6);
			}
		}

		/**
		 * Carries out the painting for all subset information which is to be
		 * overlayed on top of the overview canvas.
		 */
		void paintSubsetOverlays(Graphics2D g)
		{
			if(dragging)
			{
				g.setPaint(new Color(0, 0, 0, 125));
				g.fillRect(0, 0, l, h);
				g.fillRect(r, 0, w-r, h);
			}
		}
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