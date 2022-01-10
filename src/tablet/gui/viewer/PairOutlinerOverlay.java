// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import tablet.data.*;
import tablet.gui.*;

public class PairOutlinerOverlay implements IOverlayRenderer
{
	private ReadsCanvas rCanvas;

	private Read readA, readB;
	private int lineIndex, mateLineIndex, columnIndex;

	PairOutlinerOverlay(ReadsCanvas rCanvas)
	{
		this.rCanvas = rCanvas;
	}

	void setPair(Read readA, Read readB, int lineIndex, int mateLineIndex)
	{
		this.readA = readA;
		this.readB = readB;
		this.lineIndex = lineIndex;
		this.mateLineIndex = mateLineIndex;
	}

	public void render(Graphics2D g)
	{
		if(!isValidOutline())
			return;

		// If the reads are on the row, draw a line connecting them
		if(lineIndex == mateLineIndex && Prefs.visPaired)
			renderLinkLine(g);

		g.setColor(TabletUtils.red1);

		// Draw outlines around the reads in the pair
		renderReadOutline(readA, lineIndex, g);
		renderReadOutline(readB, mateLineIndex, g);
	}

	private void renderLinkLine(Graphics2D g)
	{
		int y = (lineIndex * rCanvas.ntH) + (rCanvas.readH / 2);
		int xS;
		int xE;
		if (readA.s() < readB.s())
		{
			xS = rCanvas.getFirstRenderedPixel(readA.e()+1);
			xE = rCanvas.getFinalRenderedPixel(readB.s()-1);
		}
		else
		{
			xS = rCanvas.getFirstRenderedPixel(readB.e()+1);
			xE = rCanvas.getFinalRenderedPixel(readA.s()-1);
		}
		g.setColor(Color.BLACK);
		g.drawLine(xS, y, xE, y);
	}

	private void renderReadOutline(Read read, int line, Graphics2D g)
	{
		int y  = line * rCanvas.ntH;
		int xS = rCanvas.getFirstRenderedPixel(read.s());
		int xE = rCanvas.getFinalRenderedPixel(read.e());

		g.drawRect(xS, y, xE-xS, rCanvas.readH-1);
	}


	/**
	 * Check if the mouse coordinates are between the start of the left read and
	 * the end of the right read, returning true if they are.
	 */
	public boolean isValidOutline()
	{
		if (readA == null || readB == null)
			return false;

		int s, e;

		if(readA.s() < readB.s())
		{
			s = readA.s();
			e = readB.e();
		}
		else
		{
			s = readB.s();
			e = readA.e();
		}

		return (columnIndex >= s && columnIndex <= e ? true : false);
	}

	public void setMateLineIndex(int mateLineIndex)
		{ this.mateLineIndex = mateLineIndex; }

	public void setColumnIndex(int columnIndex)
		{ this.columnIndex = columnIndex; }
}