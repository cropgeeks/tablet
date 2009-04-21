package av.gui.viewer;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import av.data.*;

class ConsensusCanvas extends JPanel
{
	Consensus sequence;
//	byte[] data;

	// Width and height of the canvas
	int canvasW, canvasH;
	// The total number of bases in the sequence
	int xBaseTotal;
	// The number of bases that fit on the current screen?
	int xBaseCount;
	// Width and height of the "box" a single base is drawn in
	int baseW, baseH;

	// These are the x and y pixel positions on the canvas that currently appear
	// in the top left corner of the current view
	int pX1, pY1;
	// And bottom right hand corner
	int pX2, pY2;

	// Starting and ending indices of the bases that will be drawn during the
	// next repaint operation
	private int xIndexStart, xIndexEnd;

	// Holds the current dimensions of the canvas in an AWT friendly format
	private Dimension dimension = new Dimension();

	ConsensusCanvas()
	{
		setOpaque(false);
	}

	void setConsensusSequence(Consensus sequence)
	{
		this.sequence = sequence;
//		data = sequence.getData();
	}

	// Compute canvas related dimensions that only change if the data or the
	// box-drawing size needs to be changed
	void computeDimensions(int sizeX, int sizeY)
	{
		Font font = new Font("Monospaced", Font.PLAIN, sizeY);
		FontMetrics fm = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
			.getGraphics().getFontMetrics(font);

		baseW = sizeX*2;
		baseH = fm.getHeight();

		xBaseTotal = sequence.length();

		canvasW = (xBaseTotal * baseW);
		canvasH = baseH;

		setSize(dimension = new Dimension(canvasW, canvasH));
	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;
	}
}