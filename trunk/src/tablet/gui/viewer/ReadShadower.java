// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;

import tablet.data.*;
import tablet.gui.*;

class ReadShadower implements IOverlayRenderer
{
	private final int overlayOpacity = 75;
	private final AssemblyPanel aPanel;
	private final ReadsCanvas rCanvas;

	private final Color lineColor = new Color(25, 75, 100);

	private final Color shadowColor = new Color(0, 0, 0, overlayOpacity);
	private final Color mateShadowColor = new Color(255, 0, 0, overlayOpacity);

	static Integer mouseBase;

	public ReadShadower(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;
		this.rCanvas = aPanel.readsCanvas;
	}

	/**
	 * Draws the read shadowing on top of the reads canvas.
	 */
	@Override
	public void render(Graphics2D g)
	{
		if (Prefs.visReadShadowing == 0)
			return;

		g.setPaint(shadowColor);

		int ntH = rCanvas.ntH;
		int yStart = rCanvas.pY1 / ntH;
		int yEnd = rCanvas.pY2 / ntH;

		if (Prefs.visReadShadowing == 1)
			renderLockedToMiddle(g, yStart, yEnd);

		else
			renderFreeFlowing(g, yStart, yEnd);
	}

	/**
	 * Renders the read shadowing with respect to the centre of the reads canvas.
	 * This is represented by a line down the middle of the reads canvas.
	 */
	private void renderLockedToMiddle(Graphics2D g, int yStart, int yEnd)
	{
		int midPointPixel = (rCanvas.pX1 + ((rCanvas.pX2Max-rCanvas.pX1)/2));

		int base = rCanvas.getBaseForPixel(midPointPixel);

		for (int row = yStart; row <= yEnd; row++)
		{
			Read read = rCanvas.reads.getReadAt(row, base);

			shadowRead(g, read, row);
			shadowMate(g, read);
		}
		// Draws a vertical line down the middle of the display
		g.setColor(lineColor);
		g.drawLine(midPointPixel, rCanvas.pY1, midPointPixel, rCanvas.pY2);
	}

	/**
	 * Draws the read shadowing relative to the base the mouse is currently over,
	 * or a base that the shadowing has been locked to.
	 */
	private void renderFreeFlowing(Graphics2D g, int yStart, int yEnd)
	{
		// Start by setting the position equal to the locked base
		Integer iPosition = aPanel.getVisualContig().getLockedBase();

		// But change to the mouse base if it's not locked
		if (iPosition == null)
			iPosition = mouseBase;
		// And quit if that is null too (eg, mouse not over the canvas)
		if (iPosition == null)
			return;

		for (int row = yStart; row <= yEnd; row++)
		{
			Read read = rCanvas.reads.getReadAt(row, iPosition);

			shadowRead(g, read, row);
			shadowMate(g, read);
		}
		// Draws a vertical line down the display
		g.setColor(lineColor);

		int linePos = rCanvas.getFirstRenderedPixel(iPosition)
			+ (int) rCanvas.ntW / 2;

		g.drawLine(linePos, rCanvas.pY1, linePos, rCanvas.pY2);
	}

	private void shadowRead(Graphics2D g, Read read, int row)
	{
		if (read != null && read.isNotMateLink())
		{
			int xS = rCanvas.getFirstRenderedPixel(read.s());
			int xE = rCanvas.getFinalRenderedPixel(read.e());

			g.fillRect(xS, row * rCanvas.ntH, xE-xS+1, rCanvas.readH);
		}
	}

	private void shadowMate(Graphics2D g, Read read)
	{
		// Set the colour to the pair highlight color
		g.setColor(mateShadowColor);

		if (read != null && read instanceof MatedRead)
		{
			MatedRead mRead = (MatedRead)read;
			Read mate = mRead.getMate();
			if (mate != null)
			{
				int line = rCanvas.reads.getLineForRead(mate);
				shadowRead(g, mate, line);
			}
		}

		g.setColor(shadowColor);
	}

	public static void setMouseBase(Integer newBase)
		{ mouseBase = newBase; }
}