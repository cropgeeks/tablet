package av.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

import av.data.*;

class OverviewCanvas extends JPanel
{
	private AssemblyPanel aPanel;
	private ReadsCanvas rCanvas;

	private Canvas2D canvas = new Canvas2D();

	private BufferFactory bufferFactory = null;
	private BufferedImage image = null;
	private int w, h;

	private float bX, bY, bW, bH;

	OverviewCanvas(AssemblyPanel aPanel, ReadsCanvas rCanvas)
	{
		this.aPanel = aPanel;
		this.rCanvas = rCanvas;

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(0, 75));
		add(canvas);

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				createImage();
			}
		});
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
			bufferFactory.killMe = true;
		// Before starting a new one
		bufferFactory = new BufferFactory(w, h);

		repaint();
	}

	private void bufferAvailable(BufferedImage image)
	{
		this.image = image;

		// Force the main canvas to send its view size dimensions so we can draw
		// the highlighting box on top of the new back buffer's image
		rCanvas.updateOverview();
	}

	void updateOverview(int xIndex, int xNum, int yIndex, int yNum)
	{
		if (bufferFactory == null)
			return;

		// Work out the x1/y2 position for the outline box
		bX = bufferFactory.xScale * xIndex;
		bY = bufferFactory.yScale * yIndex;

		// Work out the x2 position for the outline box
		float x2 = bX + (xNum * bufferFactory.xScale);
		if (xNum > rCanvas.ntOnCanvasX || x2 > canvas.getWidth())
			x2 = canvas.getWidth();

		// Work out the y2 position for the outline box
		float y2 = bY + (yNum * bufferFactory.yScale);
		if (yNum > rCanvas.ntOnCanvasY || y2 > canvas.getHeight())
			y2 = canvas.getHeight();

		bW = (x2-bX) - 1;
		bH = (y2-bY) - 1;

		// Set the size to 1 so something still draws if the box is very small
		if (bW < 1) bW = 1;
		if (bH < 1) bH = 1;

		repaint();
	}

	private class Canvas2D extends JPanel
	{
		Canvas2D()
		{
			setOpaque(false);

			addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent e)
					{ processMouse(e); }

				public void mousePressed(MouseEvent e)
					{ processMouse(e); }

				public void mouseReleased(MouseEvent e)
					{ processMouse(e); }
			});

			addMouseMotionListener(new MouseMotionAdapter()
			{
				public void mouseDragged(MouseEvent e)
					{ processMouse(e); }
			});
		}

		private void processMouse(MouseEvent e)
		{
			if (aPanel == null)
				return;

			int x = e.getX() - (int) (bW / 2f);
			int y = e.getY() - (int) (bH / 2f);

			// Compute mouse position (and adjust by wid/hgt of rectangle)
			int rowIndex = (int) (y / bufferFactory.yScale);
			int colIndex = (int) (x / bufferFactory.xScale);

			aPanel.moveToPosition(rowIndex, colIndex, false);
		}

		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			Graphics2D g = (Graphics2D) graphics;

			if (image == null)
				return;

			// Paint the image of the alignment
			g.drawImage(image, 0, 0, null);
	//		g.setPaint(new Color(255, 255, 255, 50));
	//		g.fillRect(0, 0, w, h);

			// Then draw the tracking rectangle
			g.setPaint(new Color(0, 0, 255, 50));
			g.fillRect(Math.round(bX), Math.round(bY), Math.round(bW), Math.round(bH));
			g.setColor(Color.red);
			g.drawRect(Math.round(bX), Math.round(bY), Math.round(bW), Math.round(bH));
		}
	}

	private class BufferFactory extends Thread
	{
		Contig contig;
		IReadManager reads;

		private BufferedImage buffer;
		private int w, h;
		private int ntOnCanvasX, ntOnCanvasY;
		private int xWidth, yHeight;
		private float xScale, yScale;

		boolean killMe = false;

		BufferFactory(int w, int h)
		{
			this.w = w;
			this.h = h;

			// Make private references to certain values now, as they MAY change
			// while the buffer is still being created, which creates a cock-up
			contig = rCanvas.contig;
			reads = rCanvas.reads;
			ntOnCanvasX = rCanvas.ntOnCanvasX;
			ntOnCanvasY = rCanvas.ntOnCanvasY;

			start();
		}

		public void run()
		{
			setPriority(Thread.MIN_PRIORITY);
			setName("OverviewCanvas BufferFactory");

			try { Thread.sleep(500); }
			catch (InterruptedException e) {}

			long s = System.nanoTime();

			if (killMe)
				return;

			// Scaling factors
			xScale = ntOnCanvasX / (float) w;
			yScale = ntOnCanvasY / (float) h;

			buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

			Graphics2D g = buffer.createGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, w, h);


			if (true)
			{
				// Loop over every pixel that makes up the overview...
				for (int y = 0; y < h && !killMe; y++)
				{
					int dataY = (int) (y * yScale);

					for (int x = 0; x < w && !killMe; x++)
					{
						// Working out where each pixel maps to in the data...
						int dataX = (int) (x * xScale) - rCanvas.offset;

						// Then drawing that data (or not)
						Read read = reads.getReadAt(dataY, dataX);
						if (read != null)
						{
							byte b = read.getStateAt(dataX-read.getStartPosition());

							g.setColor(rCanvas.colors.getColor(b));
							g.drawLine(x, y, x, y);
						}
					}
				}
			}
			else
			{
				// Loop over every pixel that makes up the overview...
				for (int y = 0; y < h && !killMe; y++)
				{
					int dataY = (int) (y * yScale);

					for (int x = 0; x < w && !killMe; x++)
					{
						// Working out where each pixel maps to in the data...
						// Each overview pixel maps to a window of data (x1 to x2)
						int dataX1 = (int) (x * xScale) - rCanvas.offset;
						int dataX2 = dataX1 + (int) xScale;

						// Determine the percentage of actual data in this window
						byte[] data = reads.getValues(dataY, dataX1, dataX2);

						float dataCount = 0;
						for (int i = 0; i < data.length; i++)
							if (data[i] != -1)
								dataCount++;

						float percent = dataCount / (float)data.length;

						// Draw the lower 10% in red
						if (percent > 0 && percent < 0.1f)
						{
							g.setColor(new Color(255, 0, 0, 100));
							g.drawLine(x, y, x, y);
						}
						// And the rest in shades of blue
						else if (percent > 0)
						{
							g.setColor(new Color(0, 0, 255, (int)(255*(percent))));
							g.drawLine(x, y, x, y);
						}
					}
				}
			}

			// Scaling factors for mouse/mapping
			xScale = w / (float) ntOnCanvasX;
			yScale = h / (float) ntOnCanvasY;

			long e = System.nanoTime();
			System.out.println("Overview time: " + ((e-s)/1000000f) + "ms");

			if (!killMe)
				bufferAvailable(buffer);
		}
	}
}