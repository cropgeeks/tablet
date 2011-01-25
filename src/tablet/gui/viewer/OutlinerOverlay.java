// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.gui.*;

/**
 * Responsible for drawing outlines around either the current read-under-the-
 * mouse, or any other supplementary features that should be drawn outlined.
 */
class OutlinerOverlay implements IOverlayRenderer
{
	private ReadsCanvasInfoPaneRenderer infoPaneRenderer;
	private ReadsCanvas rCanvas;

	private Read read;
	private int readS, readE;
	private int lineIndex;

	OutlinerOverlay(AssemblyPanel aPanel, ReadsCanvasInfoPaneRenderer infoPaneRenderer)
	{
		this.infoPaneRenderer = infoPaneRenderer;

		rCanvas = aPanel.readsCanvas;
	}

	void setRead(Read read, int colIndex, int lineIndex)
	{
		this.read = read;
		this.lineIndex = lineIndex;

		if (read != null)
		{
			ReadMetaData data = Assembly.getReadMetaData(read, false);

			// Start and ending positions (against consensus)
			readS = read.getStartPosition();
			readE = read.getEndPosition();

			infoPaneRenderer.readInfo.setData(lineIndex, read, data);
			infoPaneRenderer.readInfo.updateOverviewCanvas();

			infoPaneRenderer.pairInfo.setmRead(read);
			infoPaneRenderer.pairInfo.setMateAvailable(false);
		}
		else
			infoPaneRenderer.setMousePosition(null);
	}

	public void render(Graphics2D g)
	{
		int offset = -rCanvas.offset * rCanvas.ntW;

		// Remember the current stroke so it can be reset afterwards
		Stroke oldStroke = g.getStroke();

		// Set the outline width based on the zoom level
		if (Prefs.visReadsCanvasZoom > 18)
			g.setStroke(new BasicStroke(3));
		else if (Prefs.visReadsCanvasZoom > 8)
			g.setStroke(new BasicStroke(2));

		g.setColor(new Color(10, 10, 100));

		// Deal with any additional features first
		for (Feature f: rCanvas.contig.getOutlines())
		{
			if (f.getTabletType() == Feature.COL_OUTLINE)
			{
				int position = f.getDataPS();

				int xS = position * rCanvas.ntW + offset;
				int xE = xS + rCanvas.ntW;

				if (xS <= rCanvas.pX2 && xE > rCanvas.pX1)
					g.drawRect(xS, 0, xE-xS-1, rCanvas.getHeight()-1);
			}

			else if (f.getTabletType() == Feature.ROW_OUTLINE)
			{
				int position = f.getDataPS();

				int yS = position * rCanvas.ntH;
				int yE = yS + rCanvas.ntH;

				if (yS <= rCanvas.pY2 && yE > rCanvas.pY1)
					g.drawRect(0, yS, rCanvas.canvasW-1, yE-yS-1);
			}
		}

		// Draw an outline around whatever read is under the mouse
		if (read != null)
		{
			int y  = lineIndex * rCanvas.ntH;
			int xS = readS * rCanvas.ntW + offset;
			int xE = readE * rCanvas.ntW + rCanvas.ntW + offset;

			g.setColor(TabletUtils.red1);
			g.drawRect(xS, y, xE-xS-1, rCanvas.ntH-1);
		}

		g.setStroke(oldStroke);
	}
}