package tablet.gui.viewer;

import java.awt.*;
import java.text.*;
import javax.swing.*;

import tablet.data.*;

/** Manages and paints the advanced tool tips for the reads canvas. */
class ReadsCanvasInfoPane implements IOverlayRenderer
{
	private NumberFormat nf = NumberFormat.getInstance();
	private Color bgColor;

	private ReadsCanvas rCanvas;

	// Variables that change as we move the mouse
	private Read read;
	private ReadMetaData metaData;
	private Point mouse;
	private int x, y, w, h;

	// Variables holding the data that gets drawn within the tooltip
	private String readName;
	private int readS;
	private int readE;
	private int length;

	ReadsCanvasInfoPane()
	{
		Color c = (Color) UIManager.get("info");
		bgColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), 180);
	}

	void setReadsCanvas(ReadsCanvas rCanvas)
		{ this.rCanvas = rCanvas; }

	void setMousePosition(Point mouse)
		{ this.mouse = mouse; }

	void setData(Read read, ReadMetaData metaData)
	{
		this.read = read;
		this.metaData = metaData;

		// Start and ending positions (against consensus)
		readS = 1 + read.getStartPosition();
		readE = 1 + read.getEndPosition();
		length = (readE-readS+1);

		// Name
		readName = metaData.getName();
		// Formatted C/U plus start and end
//		String label2 = (metaData.isComplemented() ? "C: " : "U: ")
//			+ nf.format(readS) + " - " + nf.format(readE)
//			+ " (length: " + nf.format(length) + ")";

		w = 250;
		h = 50;
	}

	public void render(Graphics2D g)
	{
		if (mouse == null)
			return;

		calculatePosition();
		g.translate(x, y);


		g.setColor(bgColor);
		g.fillRoundRect(0, 0, w-1, h-1, 10, 10);
		g.setColor(Color.black);
		g.drawRoundRect(0, 0, w-1, h-1, 10, 10);

		g.drawString(readName, 10, 15);
		g.drawString(readS + "-" + readE, 10, 30);


		// Put the graphics origin back to where it was in case other overlay
		// renderers run after this one
		g.translate(-x, -y);
	}

	private void calculatePosition()
	{
		// Decide where to draw (roughly)
		x = mouse.x + 15;
		y = mouse.y + 20;

		// Then adjust if the box would be offscreen to the right or bottom
		if (x + w >= rCanvas.pX2Max)
			x = rCanvas.pX2Max - w - 1;
		if (y + h >= rCanvas.pY2)
			y = rCanvas.pY2 - h - 1;
	}
}