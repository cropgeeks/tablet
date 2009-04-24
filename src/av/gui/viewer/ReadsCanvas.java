package av.gui.viewer;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import av.data.*;

class ReadsCanvas extends JPanel
{
	private Contig contig;
	private IReadManager reads;

	// Width and height of the canvas
	int canvasW, canvasH;

	// Width and height of a single nucleotide when it is drawn
	int ntW, ntH;
	// The number of nucleotides that fit on the current screen?
	int ntOnScreenX, ntOnScreenY;
	// And the total number of nucleotides that span the entire canvas
	int ntOnCanvasX, ntOnCanvasY;

	// These are the x and y pixel positions on the canvas that currently appear
	// in the top left corner of the current view
	int pX1, pY1;
	// And bottom right hand corner
	int pX2, pY2;

	// Starting and ending indices of the bases that will be drawn during the
	// next repaint operation
	private int xS, xE, yS, yE;

	// Holds the current dimensions of the canvas in an AWT friendly format
	private Dimension dimension = new Dimension();

	ReadsCanvas()
	{
		setOpaque(false);
	}

	void setContig(Contig contig)
	{
		this.contig = contig;
		reads = contig.getReadManager();

		computeDimensions(5, 5);
	}

	// Compute canvas related dimensions that only change if the data or the
	// box-drawing size needs to be changed
	void computeDimensions(int sizeX, int sizeY)
	{
		Font font = new Font("Monospaced", Font.PLAIN, sizeY);
		FontMetrics fm = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
			.getGraphics().getFontMetrics(font);

		ntW = sizeX*2;
		ntH = fm.getHeight();

		System.out.println("ntW=" + ntW);
		System.out.println("ntH=" + ntH);

		ntOnCanvasX = contig.getWidth();
		ntOnCanvasY = contig.getHeight();

		System.out.println("ntOnCanvasX=" + ntOnCanvasX);
		System.out.println("ntOnCanvasY=" + ntOnCanvasY);

		canvasW = (ntOnCanvasX * ntW);
		canvasH = (ntOnCanvasY * ntH);

		System.out.println("Canvas size: " + canvasW + "x" + canvasH);

		setSize(dimension = new Dimension(canvasW, canvasH));
	}

	// Compute real-time variables, that change as the viewpoint is moved across
	// the canvas
	void computeForRedraw(Dimension viewSize, Point viewPosition)
	{
		ntOnScreenX = 1 + (int) ((float) viewSize.getWidth()  / ntW);
		ntOnScreenY = 1 + (int) ((float) viewSize.getHeight() / ntH);

		pX1 = viewPosition.x;
		pY1 = viewPosition.y;

		pX2 = pX1 + viewSize.width;
		pY2 = pY1 + viewSize.height;

		repaint();
	}

	public Dimension getPreferredSize()
		{ return dimension; }

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;


		// Index positions within the dataset that we'll start drawing from
		xS = pX1 / ntW;
		yS = pY1 / ntH;

		// The end indices are calculated as the:
		//   (the start index) + (the number that can be drawn on screen)
		// with a check to set the end index to the last value in the array if
		// the calculated index would go out of bounds
		xE = xS + ntOnScreenX;
		if (xE >= ntOnCanvasX)
			xE = ntOnCanvasX-1;

		yE = yS + ntOnScreenY;
		if (yE >= ntOnCanvasY)
			yE = ntOnCanvasY-1;


		System.out.println("X: " + xS + "->" + xE);
		System.out.println("Y: " + yS + "->" + yE);
		System.out.println();


		// For each row...
		for (int row = yS, y = (ntH*yS); row <= yE; row++, y += ntH)
		{
			byte[] data = reads.getValues(row, xS, xE);

			for (int i = 0, x = (ntW*xS); i < data.length; i++, x += ntW)
			{
				if (data[i] == -1)
					g.fillRect(x, y, ntW, ntH);
				else
					g.drawString("" + data[i], x, y);
			}
		}



/*		g.setColor(Color.red);
		g.fillRect(0, 0, getWidth(), getHeight());

		g.setColor(Color.white);
		g.fillRect(0, 0, canvasW, canvasH);

		g.setColor(Color.black);
		for (int i = 0, cI=0; i < canvasW; i += ntW, cI++)
			for (int j = 0, cJ=0; j < canvasH; j += ntH, cJ++)
			{
				g.drawRect(i, j, ntW, ntH);

				if (cI % 50 == 0 && cJ % 50 == 0)
					g.fillRect(i, j, ntW, ntH);
			}
*/
	}
}