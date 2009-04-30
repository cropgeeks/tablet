package av.gui.viewer;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import av.data.*;

class OverviewCanvas extends JPanel
{
	private AssemblyPanel aPanel;
	private ReadsCanvas rCanvas;

	private Canvas2D canvas = new Canvas2D();

	OverviewCanvas(AssemblyPanel aPanel, ReadsCanvas rCanvas)
	{
		this.aPanel = aPanel;
		this.rCanvas = rCanvas;

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(0, 75));
		add(canvas);
	}

	private class Canvas2D extends JPanel
	{
		Contig contig;
		IReadManager reads;

		private int w, h;
		private int ntOnCanvasX, ntOnCanvasY;
		private int xWidth, yHeight;
		private float xScale, yScale;

		boolean killMe = false;

		Canvas2D()
		{
			setOpaque(false);
		}

		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			w = getSize().width;
			h = getSize().height;

			contig = rCanvas.contig;

			if (contig == null)
				return;

			reads = rCanvas.reads;

			// Make private references to certain values now, as they MAY change
			// while the buffer is still being created, which creates a cock-up
			ntOnCanvasX = rCanvas.ntOnCanvasX;
			ntOnCanvasY = rCanvas.ntOnCanvasY;

			// Scaling factors
			xScale = ntOnCanvasX / (float) w;
			yScale = ntOnCanvasY / (float) h;

//			Graphics2D g = buffer.createGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, w, h);


			// Loop over every pixel that makes up the overview...
			for (int y = 0; y < h; y++)
			{
				for (int x = 0; x < w; x++)
				{
					// Working out where each pixel maps to in the data...
					int dataX = (int) (x * xScale);
					int dataY = (int) (y * yScale);

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
	}
}