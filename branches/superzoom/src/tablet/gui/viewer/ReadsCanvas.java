// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;

import tablet.data.*;
import tablet.gui.*;
import tablet.gui.viewer.colors.*;

public class ReadsCanvas extends JPanel
{
	private AssemblyPanel aPanel;

	BufferedImage buffer;
	private boolean updateBuffer = true;

	Contig contig;
	IReadManager reads;

	// Color scheme in use
	ReadScheme colors;
	ProteinScheme proteins;

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
	int xS, xE, yS, yE;

	// Tracks the base closest to the centre of the view
	float ntCenterX, ntCenterY;

	// Holds the current dimensions of the canvas in an AWT friendly format
	private Dimension dimension = new Dimension();

	// A list of renderers that will perform further drawing once the main
	// canvas has been drawn
	LinkedList<IOverlayRenderer> overlays = new LinkedList<IOverlayRenderer>();

	// Objects for multicore rendering
	private int cores = Runtime.getRuntime().availableProcessors();
	private ExecutorService executor;
	private Future[] tasks;

	// Set to true if the canvas is rendered (ie, generated the back buffer)
	boolean isRendering = false;

	ReadsCanvasML readsCanvasML;

	float _ntW;
	int _ntOnCanvasX, _canvasW;
	int _ntOnScreenX;
	int _pixelsOnScreenX;

	ReadsCanvas()
	{
		setOpaque(false);

		// Prepare the background threads that will do the main painting
		executor = Executors.newFixedThreadPool(cores);
		tasks = new Future[cores];
	}

	void setAssemblyPanel(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;
		readsCanvasML = new ReadsCanvasML(aPanel);

		aPanel.toggleNameOverlay();
	}

	void setContig(Contig contig)
	{
		this.contig = contig;

		if (contig != null)
		{
			if (Prefs.visPacked && !Prefs.visPaired)
				reads = contig.getPackManager();

			else if(Prefs.visPacked && Prefs.visPaired)
				reads = contig.getPairedPackManager();

			else if(!Prefs.visPacked && Prefs.visPaired)
				reads = contig.getPairedStackManager();

			else
				reads = contig.getStackManager();

			offset = contig.getVisualStart();
		}

		// We need to ensure that any references to tablet.data objects are
		// removed, otherwise they can't get garbage collected
		else
			reads = null;
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

		// If greater than 10 use current font based scaling, otherwise 10 == 1:1
		// scaling and every notch below that double the zoom out.
		ntW = sizeX*2;

		if (sizeX > 15)
			_ntW = sizeX * 2;
		else
			_ntW = (float) (1 / Math.pow(2, (15-sizeX)));

		ntH = fm.getHeight();

		System.out.println("New _NTW: " + Math.pow(2, (15-sizeX)));

		ntOnCanvasX = contig.getVisualWidth();
		ntOnCanvasY = contig.getVisualHeight();

		_ntOnCanvasX = contig.getVisualWidth();

		canvasW = (ntOnCanvasX * ntW);
		canvasH = (ntOnCanvasY * ntH);

		// Round UP to cope with 3.3 pixels needed for ten bases at 0.3 pixels per base (4 pixels actually needed)
		_canvasW = (int)Math.ceil(_ntOnCanvasX * _ntW);

		if (_canvasW < 1)
			_canvasW = 1;

//		System.out.println("_ntW: " + _ntW + " _ntOnCnavasX: " + _ntOnCanvasX + " _canvasW: " + _canvasW);

		//dimension = new Dimension(canvasW, canvasH);
		dimension = new Dimension(_canvasW, canvasH);

		updateColorScheme();
	}

	// Compute real-time variables, that change as the viewpoint is moved across
	// the canvas
	void computeForRedraw(Dimension viewSize, Point viewPosition)
	{
		if (contig == null)
			return;

		ntOnScreenX = 1 + (int) ((float) viewSize.width  / ntW);
		ntOnScreenY = 1 + (int) ((float) viewSize.height / ntH);

		_pixelsOnScreenX = viewSize.width;
		_ntOnScreenX = 1 + (int) ((float) viewSize.width / _ntW);

//		System.out.println("ntOnScreenX: " + _ntOnScreenX);

		pX1 = viewPosition.x;
		pX2 = pX2Max = pX1 + viewSize.width -1;
		// Adjust for canvases that are smaller than the window size
		//if (pX2 > canvasW)
		//	pX2 = canvasW - 1;

		if (pX2 > _canvasW)
			pX2 = _canvasW -1;

		pY1 = viewPosition.y;
		pY2 = pY1 + viewSize.height - 1;

		// Track the base closest to the center of the current view
		ntCenterX = (pX1 / ntW)	+ ((viewSize.width  / ntW) / 2);
		ntCenterY = (pY1 / ntH)	+ ((viewSize.height / ntH) / 2);

		updateOverview();
		updateBuffer = true;

		repaint();
	}

	void updateOverview()
	{
		int xS = (pX1/ntW);
		int yS = (pY1/ntH);

		aPanel.canvasViewChanged(xS, ntOnScreenX, yS, ntOnScreenY);
	}

	void updateColorScheme()
	{
		colors = ReadScheme.getScheme(Prefs.visColorScheme, ntW, ntH);
		proteins = ProteinScheme.getScheme(Prefs.visColorScheme, ntW, ntH);

		updateBuffer = true;
		repaint();
	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;

		long s = System.nanoTime();

		// Update the back buffer (if it needs redrawn)
		if (updateBuffer)
		{
			isRendering = true;

			try { paintBuffer(); }
			catch (Exception e) {}

			isRendering = false;

			yS = pY1 / ntH;
			new VisibleReadsGrabberThread(reads, xS+offset, xE+offset, yS, yE);
			new SnapshotGrabber(this, aPanel.getSnapshotController());
		}

		// Then paint it to the screen
		g.drawImage(buffer, pX1, pY1, null);

		// Then allow any overlays to be painted on top of it
		try
		{
			for (IOverlayRenderer renderer: overlays)
				renderer.render(g);
		}
		catch (ConcurrentModificationException e) {
			repaint();
		}

		long e = System.nanoTime();
		System.out.println("Render time: " + ((e-s)/1000000f) + "ms");
	}

	private void paintBuffer()
		throws Exception
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
		//g.fillRect(0, 0, -offset*ntW, getHeight());
		g.fillRect(0, 0, (int)(-offset*_ntW), getHeight());
		int cLength = -offset + contig.getConsensus().length();
		//g.fillRect(cLength*ntW, 0, canvasW-(cLength*ntW), getHeight());
		g.fillRect((int)(cLength*_ntW), 0, canvasW-(int)(cLength*_ntW), getHeight());


		// Index positions within the dataset that we'll start drawing from
		//xS = pX1 / ntW;
		xS = (int)(pX1 / _ntW);
		int yS = pY1 / ntH;

		// The end indices are calculated as the:
		//   (the start index) + (the number that can be drawn on screen)
		// with a check to set the end index to the last value in the array if
		// the calculated index would go out of bounds
		//xE = xS + ntOnScreenX;
		//if (xE >= ntOnCanvasX) xE = ntOnCanvasX-1;
		xE = xS + _ntOnScreenX;
		if (xE >= _ntOnCanvasX) xE = _ntOnCanvasX-1;

		yE = yS + ntOnScreenY;
		if (yE >= ntOnCanvasY) yE = ntOnCanvasY-1;

		// Paint the lines using multiple cores...
		for (int i = 0; i < tasks.length; i++)
			tasks[i] = executor.submit(new LinePainter(g, yS+i));
		for (Future task: tasks)
			task.get();

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

	private final class LinePainter implements Runnable
	{
		private Graphics g;
		private int yS;

		LinePainter(Graphics g, int yS)
		{
			this.g = g;
			this.yS = yS;
		}

		public void run()
		{
			// Declared here rather below because it did seem to be ever so
			// slightly faster (not having to allocate them on every iteration).
			LineData data;
			ReadMetaData[] rmds;
			int[] indexes;
			Read[] readArr;

			PackRow.count = 0;
			long total = 0;

			// For every [nth] row, where n = number of available CPU cores...
			for (int row = yS, y = (ntH*yS); row <= yE; row += cores, y += ntH*cores)
			{
				long s = System.nanoTime();
				data = reads.getPixelData(row, xS+offset, _pixelsOnScreenX, _ntW);
				total += System.nanoTime() - s;

				// Ask the read manager to calculate the data for this row
				rmds = data.getRMDs();
				indexes = data.getIndexes();
				readArr = data.getReads();

				if (_ntW > 1)
				{
					for (int i = 0, x = (int)(_ntW*xS); i < _pixelsOnScreenX; i += _ntW, x += _ntW)
					{
						if (indexes[i] >= 0 && readArr[i] != null)
							g.drawImage(colors.getImage(rmds[i], indexes[i]), x, y, null);

						else if (indexes[i] == LineData.PAIRLINK)
							g.drawImage(colors.getPairLink(), x, y, null);
					}
				}
				else
				{
					g.setColor(Color.red);
					for (int i = 0, x = (int)(_ntW*xS); i < _pixelsOnScreenX; i++, x++)
					{
						if (readArr[i] != null)
							g.drawLine(x, y, x, y);
					}
				}
			}

//	data = reads.getPixelData(row, xS, _pixelsOnScreenX, /*pixels2Bases*/ _ntW);
//float pixels2Bases = /*_pixelsOnScreenX*/ canvasW / (float) ntOnCanvasX;
//			long e = System.nanoTime();
//			float average = ((e-s)/(float)rowCount);
			System.out.println("Total Data time: " + (total/1000000f) + "ms");

//			System.out.println("Binary searches: " + PackRow.count);
			System.out.println();

		}
	}

	public int getXS()
	{
		return xS;
	}

	public int getXE()
	{
		return xE;
	}

	public int getYS()
	{
		return yS;
	}

	public int getYE()
	{
		return yE;
	}
}