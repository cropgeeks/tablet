// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.viewer.colors.*;

/**
 * Responsible for drawing outlines around either the current read-under-the-
 * mouse, or any other supplementary features that should be drawn outlined.
 */
class OutlinerOverlay implements IOverlayRenderer
{
	private ReadsCanvas rCanvas;

	private Read read;
	private int lineIndex;

	OutlinerOverlay(AssemblyPanel aPanel)
	{
		rCanvas = aPanel.readsCanvas;
	}

	void setRead(Read read, int colIndex, int lineIndex)
	{
		this.read = read;
		this.lineIndex = lineIndex;
	}

	public void render(Graphics2D g)
	{
		g.setColor(ColorPrefs.get("User.OutlinerOverlay.RowColOutliner"));

		// Deal with any additional features first
		for (VisualOutline f: rCanvas.contig.getOutlines())
		{
			if (f.type == VisualOutline.READ)
			{
				int xS = rCanvas.getFirstRenderedPixel(f.value1);
				int xE = rCanvas.getFinalRenderedPixel(f.value2);
				int y  = f.value3 * rCanvas.ntH;

				if (xS <= rCanvas.pX2 && xE > rCanvas.pX1)
					g.drawRect(xS, y, xE-xS, rCanvas.readH-1);
			}

			else if (f.type == VisualOutline.COL)
			{
				int xS = rCanvas.getFirstRenderedPixel(f.value1);
				int xE = rCanvas.getFinalRenderedPixel(f.value1);

				// "Hack" to deal with ensuring a one-pixel wide base stays on
				// screen at all zoom levels (remember the methods above were
				// written to deal with Reads and how they are rendered)
				if (rCanvas.ntW < 1)
					xS = rCanvas.getFinalRenderedPixel(f.value1);

				if (xS <= rCanvas.pX2 && xE > rCanvas.pX1)
					g.drawRect(xS, 0, xE-xS, rCanvas.getHeight()-1);
			}

			else if (f.type == VisualOutline.ROW)
			{
				int yS = f.value1 * rCanvas.ntH;
				int yE = yS + rCanvas.ntH;

				if (yS <= rCanvas.pY2 && yE > rCanvas.pY1)
					g.drawRect(0, yS, rCanvas._canvasW-1, yE-yS-1);
			}
		}

		// Draw an outline around whatever read is under the mouse
		if (read != null)
		{
			int xS = rCanvas.getFirstRenderedPixel(read.s());
			int xE = rCanvas.getFinalRenderedPixel(read.e());
			int y  = lineIndex * rCanvas.ntH;

			g.setPaint(new Color(255, 255, 255, 75));
			g.fillRect(xS, y, xE-xS, rCanvas.readH-1);
			g.setColor(ColorPrefs.get("User.OutlinerOverlay.ReadOutliner"));
			g.drawRect(xS, y, xE-xS, rCanvas.readH-1);
		}
	}
}