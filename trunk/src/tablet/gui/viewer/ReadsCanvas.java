package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;

import tablet.data.*;
import tablet.gui.*;
import tablet.gui.viewer.colors.*;

class ReadsCanvas extends JPanel
{
	private AssemblyPanel aPanel;

	private BufferedImage buffer;
	private boolean updateBuffer = true;

	Contig contig;
	IReadManager reads;

	// Color scheme in use
	ColorScheme colors;

	// Width and height of the canvas
	int canvasW, canvasH;

	// Width and height of a single nucleotide when it is drawn
	int ntW, ntH;
	// The number of nucleotides that fit on the current screen?
	int ntOnScreenX, ntOnScreenY;
	// And the total number of nucleotides that span the entire canvas
	int ntOnCanvasX, ntOnCanvasY;

	// The LHS offset (difference) between the left-most read and the consensus
	int offset;

	// These are the x and y pixel positions on the canvas that currently appear
	// in the top left corner of the current view
	int pX1, pY1;
	// And bottom right hand corner
	int pX2, pX2Max, pY2;

	// Starting and ending indices of the bases that will be drawn during the
	// next repaint operation
	int xS, xE;

	// Holds the current dimensions of the canvas in an AWT friendly format
	private Dimension dimension = new Dimension();

	// A list of renderers that will perform further drawing once the main
	// canvas has been drawn
	LinkedList<IOverlayRenderer> overlays = new LinkedList<IOverlayRenderer>();


	ReadsCanvas()
	{
		setOpaque(false);

		// Set up some keyboard navigation
		Action pageLeft = new AbstractAction() {
			public void actionPerformed(ActionEvent e) { aPanel.pageLeft(); }
		};
		Action pageRight = new AbstractAction() {
			public void actionPerformed(ActionEvent e) { aPanel.pageRight(); }
		};

		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
			KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, 0), "left");
		getActionMap().put("left", pageLeft);

		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
			KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, 0), "right");
		getActionMap().put("right", pageRight);
	}

	void setAssemblyPanel(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;
		new ReadsCanvasML(aPanel);
	}

	void setContig(Contig contig)
	{
		this.contig = contig;

		if (contig != null)
		{
			if (Prefs.visPacked)
				reads = contig.getPackSetManager();
			else
				reads = contig.getStackSetManager();

			offset = contig.getConsensusOffset();
		}
	}

	public Dimension getPreferredSize()
		{ return dimension; }

	// Compute canvas related dimensions that only change if the data or the
	// box-drawing size needs to be changed
	void setDimensions(int sizeX, int sizeY)
	{
		if (contig == null)
			return;

		Font font = new Font("Monospaced", Font.PLAIN, sizeY);
		FontMetrics fm = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
			.getGraphics().getFontMetrics(font);

		ntW = sizeX*2;
		ntH = fm.getHeight();

		ntOnCanvasX = contig.getWidth();
		ntOnCanvasY = contig.getHeight();

		canvasW = (ntOnCanvasX * ntW);
		canvasH = (ntOnCanvasY * ntH);

		setSize(dimension = new Dimension(canvasW, canvasH));
		aPanel.setScrollbarAdjustmentValues(ntW, ntH);

		updateColorScheme();
		updateBuffer = true;
	}

	// Compute real-time variables, that change as the viewpoint is moved across
	// the canvas
	void computeForRedraw(Dimension viewSize, Point viewPosition)
	{
		if (contig == null)
			return;

		ntOnScreenX = 1 + (int) ((float) viewSize.getWidth()  / ntW);
		ntOnScreenY = 1 + (int) ((float) viewSize.getHeight() / ntH);

		pX1 = viewPosition.x;
		pX2 = pX2Max = pX1 + viewSize.width -1;
		// Adjust for canvases that are smaller than the window size
		if (pX2 > canvasW)
			pX2 = canvasW - 1;

		pY1 = viewPosition.y;
		pY2 = pY1 + viewSize.height - 1;

		updateOverview();
		updateBuffer = true;

		repaint();
	}

	void updateOverview()
	{
		int xS = (pX1/ntW);
		int yS = (pY1/ntH);

		aPanel.updateOverview(xS, ntOnScreenX, yS, ntOnScreenY);
	}

	private void updateColorScheme()
	{
		switch (Prefs.visColorScheme)
		{
			case ColorScheme.STANDARD:
				colors = new StandardColorScheme(contig, ntW, ntH);
				break;

			case ColorScheme.TEXT:
				colors = new TextColorScheme(contig, ntW, ntH);
				break;

			default:
				colors = new StandardColorScheme(contig, ntW, ntH);
		}
	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;

		if (contig == null)
			return;

		long s = System.nanoTime();

		if (updateBuffer)
			paintBuffer();

		g.drawImage(buffer, pX1, pY1, null);

		try
		{
			for (IOverlayRenderer renderer: overlays)
				renderer.render(g);
		}
		catch (ConcurrentModificationException e) {
			repaint();
		}

		long e = System.nanoTime();
//		System.out.println("Render time: " + ((e-s)/1000000f) + "ms");
	}

	private void paintBuffer()
	{
		validateBuffer();

		Graphics2D g = buffer.createGraphics();

		// Clear the background
		int bgValue = 255 - Prefs.visVariantAlpha;
		g.setColor(new Color(bgValue, bgValue, bgValue));
		g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());

		g.translate(-pX1, -pY1);

		// Calculate and draw the blue/gray background for offset regions
		g.setColor(new Color(240, 240, 255));
		g.fillRect(0, 0, offset*ntW, getHeight());
		int cLength = offset + contig.getConsensus().length();
		g.fillRect(cLength*ntW, 0, canvasW-(cLength*ntW), getHeight());


		// Index positions within the dataset that we'll start drawing from
		xS = pX1 / ntW;
		int yS = pY1 / ntH;

		// The end indices are calculated as the:
		//   (the start index) + (the number that can be drawn on screen)
		// with a check to set the end index to the last value in the array if
		// the calculated index would go out of bounds
		xE = xS + ntOnScreenX;
		if (xE >= ntOnCanvasX)
			xE = ntOnCanvasX-1;

		int yE = yS + ntOnScreenY;
		if (yE >= ntOnCanvasY)
			yE = ntOnCanvasY-1;


		// For each row...
		for (int row = yS, y = (ntH*yS); row <= yE; row++, y += ntH)
		{
			byte[] data = reads.getValues(row, xS-offset, xE-offset);

			for (int i = 0, x = (ntW*xS); i < data.length; i++, x += ntW)
			{
				if (data[i] != -1)
					g.drawImage(colors.getImage(data[i]), x, y, null);
			}
		}

		g.dispose();
		updateBuffer = false;
	}

	private void validateBuffer()
	{
		// Work out the width and height needed
		int w = pX2-pX1+1;
		int h = pY2-pY1+1;

		// Only make a new buffer if we really really need to, as this has
		// a noticeable effect on performance because of the time it takes
		if (buffer == null || buffer.getWidth() != w || buffer.getHeight() != h)
			buffer = (BufferedImage) createImage(w, h);
	}
}