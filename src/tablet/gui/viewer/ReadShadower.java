// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.Color;
import java.awt.Graphics2D;

import tablet.data.Read;
import tablet.gui.Prefs;

class ReadShadower implements IOverlayRenderer
{
	private int overlayOpacity = 75;
	private AssemblyPanel aPanel;
	private ReadsCanvas rCanvas;

	private Color lineColor = new Color(25, 75, 100);

	static Integer mouseBase;

	public ReadShadower(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;
		this.rCanvas = aPanel.readsCanvas;
	}

	/**
	 * Draws the read shadowing on top of the reads canvas.
	 */
	public void render(Graphics2D g)
	{
		if (Prefs.visReadShadowing == 0)
			return;

		g.setPaint(new Color(0, 0, 0, overlayOpacity));

		if (Prefs.visReadShadowing == 1)
			renderLockedToMiddle(g);

		else
			renderFreeFlowing(g);
	}

	/**
	 * Renders the read shadowing with respect to the centre of the reads canvas.
	 * This is represented by a line down the middle of the reads canvas.
	 */
	private void renderLockedToMiddle(Graphics2D g)
	{
		int mid = (rCanvas.pX1 + ((rCanvas.pX2Max-rCanvas.pX1)/2));
		int top = rCanvas.pY1 / rCanvas.ntH;
		int bottom = rCanvas.pY2 / rCanvas.ntH;
		int ntH = rCanvas.ntH;
		int ntW = rCanvas.ntW;
		int offset = rCanvas.offset;

		for (int row = top; row <= bottom; row++)
		{
			Read read = rCanvas.reads.getReadAt(row, mid/ntW+offset);
			
			if (read != null)
				g.fillRect((read.getStartPosition()-offset) * ntW, row * ntH, (read.getEndPosition()-read.getStartPosition()+1) * ntW, ntH);
		}
		// Draws a vertical line down the middle of the display
		g.setColor(lineColor);
		g.drawLine(mid, rCanvas.pY1, mid, rCanvas.pY2);
	}

	/**
	 * Draws the read shadowing relative to the base the mouse is currently over,
	 * or a base that the shadowing has been locked to.
	 */
	private void renderFreeFlowing(Graphics2D g)
	{
		// Start by setting the position equal to the locked base
		Integer iPosition = aPanel.getVisualContig().getLockedBase();

		// But change to the mouse base if it's not locked
		if (iPosition == null)
			iPosition = mouseBase;
		// And quit if that is null too (eg, mouse not over the canvas)
		if (iPosition == null)
			return;

		int ntH = rCanvas.ntH;
		int ntW = rCanvas.ntW;
		int offset = rCanvas.offset;
		int yS = rCanvas.pY1 / ntH;
		int yE = rCanvas.pY2 / ntH;
		
		for (int row = yS; row <= yE; row++)
		{
			Read read = rCanvas.reads.getReadAt(row, iPosition);
			
			if (read != null)
				g.fillRect((read.getStartPosition() - offset) * ntW, row * ntH, ((read.getEndPosition() - offset) - (read.getStartPosition() - offset) + 1) * ntW, ntH);
		}
		// Draws a vertical line down the display
		g.setColor(lineColor);

		int linePos = (iPosition - offset) * ntW + ntW / 2;
		g.drawLine(linePos, rCanvas.pY1, linePos, rCanvas.pY2);
	}

	public static void setMouseBase(Integer newBase)
		{ mouseBase = newBase; }
}