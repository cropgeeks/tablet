// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.*;
import tablet.gui.viewer.colors.*;

/**
 * Responsible for drawing outlines around either the current read-under-the-
 * mouse, or any other supplementary features that should be drawn outlined.
 */
class OutlinerOverlay implements IOverlayRenderer
{
	private ReadsCanvas rCanvas;

	private Read read;
	private int readS, readE;
	private int lineIndex;

	OutlinerOverlay(AssemblyPanel aPanel)
	{
		rCanvas = aPanel.readsCanvas;
	}

	void setRead(Read read, int colIndex, int lineIndex)
	{
		this.read = read;
		this.lineIndex = lineIndex;

		if (read != null)
		{
			// Start and ending positions (against consensus)
			readS = read.s();
			readE = read.e();
		}
	}

	public void render(Graphics2D g)
	{
		g.setColor(ColorPrefs.get("User.OutlinerOverlay.RowColOutliner"));

		// Deal with any additional features first
		for (VisualOutline f: rCanvas.contig.getOutlines())
		{
			if (f.type == VisualOutline.READ)
			{
				int xS = (int) Math.ceil((f.value1-rCanvas.offset) * rCanvas._ntW);
				int xE = (int) Math.floor((f.value2-rCanvas.offset) * rCanvas._ntW);
				int y  = f.value3 * rCanvas.ntH;

				if (rCanvas._ntW > 1)
					xE += rCanvas._ntW - 1;

				if (xS <= rCanvas.pX2 && xE > rCanvas.pX1)
					g.drawRect(xS, y, xE-xS, rCanvas.readH-1);
			}

			else if (f.type == VisualOutline.COL)
			{
				int xS = (int) Math.ceil((f.value1-rCanvas.offset) * rCanvas._ntW);
				int xE = xS;

				if (rCanvas._ntW > 1)
					xE += rCanvas._ntW - 1;

				if (xS <= rCanvas.pX2 && xE > rCanvas.pX1)
					g.drawRect(xS, 0, xE-xS, rCanvas.getHeight()-1);
			}

			else if (f.type == VisualOutline.ROW)
			{
				int yS = f.value1 * rCanvas.ntH;
				int yE = yS + rCanvas.ntH;

				if (yS <= rCanvas.pY2 && yE > rCanvas.pY1)
					g.drawRect(0, yS, rCanvas.canvasW-1, yE-yS-1);
			}
		}

		// Draw an outline around whatever read is under the mouse
		if (read != null)
		{
			// 1st base ceiled, because it might map to pixel 5.5 - the render
			// code will have looked at 5.0 and decided no read gets drawn on it
			// and starts at 6 instead
			int xS = (int) Math.ceil((readS-rCanvas.offset) * rCanvas._ntW);
			// Last base floored, because if it ends at 7.7 then the last pixel
			// painted by the renderer will be 7
			int xE = (int) Math.floor((readE-rCanvas.offset) * rCanvas._ntW);
			int y  = lineIndex * rCanvas.ntH;

			// Compensate for zoom levels where 1 base is greater than 1 pixel
			if (rCanvas._ntW > 1)
				xE += rCanvas._ntW - 1;

			g.setPaint(new Color(255, 255, 255, 75));
			g.fillRect(xS, y, xE-xS, rCanvas.readH-1);
			g.setColor(ColorPrefs.get("User.OutlinerOverlay.ReadOutliner"));
			g.drawRect(xS, y, xE-xS, rCanvas.readH-1);
		}
	}
}