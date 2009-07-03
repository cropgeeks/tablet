package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

import tablet.data.*;

class OverviewCanvas extends JPanel
{
	private AssemblyPanel aPanel;
	private ReadsCanvas rCanvas;

	private Canvas2D canvas = new Canvas2D();

	private BufferFactory bufferFactory = null;
	private BufferedImage image = null;
	private int w, h;

	private int bX1, bY1, bX2, bY2;

	private boolean basicView = true;

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

		// Mouse listener for the canvas
		canvas.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
				{
					basicView = !basicView;
					createImage();
				}
				else
					canvas.processMouse(e);
			}

			public void mousePressed(MouseEvent e)
				{ canvas.processMouse(e); }

			public void mouseReleased(MouseEvent e)
				{ canvas.processMouse(e); }
		});

		canvas.addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent e)
				{ canvas.processMouse(e); }
		});
	}

	void setAssemblyPanel(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;
		rCanvas = aPanel.readsCanvas;
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

		// Work out the x1 position for the outline box
		bX1 = Math.round(bufferFactory.xScale * xIndex);
		if (bX1 >= w) bX1 = w - 1;

		// Work out the y1 position for the outline box
		bY1 = Math.round(bufferFactory.yScale * yIndex);
		if (bY1 >= h) bY1 = h - 1;

		// Work out the x2 position for the outline box
		bX2 = bX1 + Math.round(xNum * bufferFactory.xScale);
		if (bX2 >= w) bX2 = w - 1;

		// Work out the y2 position for the outline box
		bY2 = bY1 + Math.round(yNum * bufferFactory.yScale);
		if (bY2 >= h) bY2 = h - 1;

		// Tweak for a fatter outline on very large canvases
		if (bX1 == bX2) bX2++;
		if (bY1 == bY2) bY2++;

		repaint();
	}

	private class Canvas2D extends JPanel
	{
		Canvas2D()
		{
			setOpaque(false);
		}

		private void processMouse(MouseEvent e)
		{
			if (aPanel == null)
				return;

			// Compute mouse position (and adjust by wid/hgt of rectangle)
			int x = e.getX() - (int) ((bX2-bX1+1) / 2f);
			int y = e.getY() - (int) ((bY2-bY1+1) / 2f);

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
			g.fillRect(bX1, bY1, bX2-bX1, bY2-bY1);
			g.setColor(Color.red);
			g.drawRect(bX1, bY1, bX2-bX1, bY2-bY1);
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


			if (basicView)
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