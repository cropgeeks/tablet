// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import tablet.data.*;
import tablet.gui.*;

public class PairOutlinerOverlay implements IOverlayRenderer
{
	private ReadsCanvasInfoPaneRenderer infoPaneRenderer;
	private ReadsCanvas rCanvas;

	private Read readA, readB;
	private int lineIndex, mateLineIndex, columnIndex;

	PairOutlinerOverlay(ReadsCanvas rCanvas, ReadsCanvasInfoPaneRenderer infoPaneRenderer)
	{
		this.infoPaneRenderer = infoPaneRenderer;
		this.rCanvas = rCanvas;
	}

	void setPair(Read readA, Read readB, int lineIndex, int mateLineIndex)
	{
		this.readA = readA;
		this.readB = readB;
		this.lineIndex = lineIndex;
		this.mateLineIndex = mateLineIndex;

		if (readA != null)
		{
			ReadMetaData data = Assembly.getReadMetaData(readA, false);
			infoPaneRenderer.readInfo.setData(lineIndex, readA, data);
			infoPaneRenderer.readInfo.updateOverviewCanvas();

			infoPaneRenderer.pairInfo.setmRead(readA);
		}

		if (readB != null)
		{
			ReadMetaData pairData = Assembly.getReadMetaData(readB, false);
			infoPaneRenderer.pairInfo.setData(mateLineIndex, readB, pairData);

			infoPaneRenderer.readInfo.setmRead(readB);
			infoPaneRenderer.pairInfo.setMateAvailable(true);
		}
	}

	public void render(Graphics2D g)
	{
		if(!isValidOutline())
			return;

		int ntW = rCanvas.ntW;
		int ntH = rCanvas.ntH;
		int offset = -rCanvas.offset * ntW;

		// Remember the current stroke so it can be reset afterwards
		Stroke oldStroke = g.getStroke();

		// Set the outline width based on the zoom level
		if (Prefs.visReadsCanvasZoom > 18)
			g.setStroke(new BasicStroke(3));
		else if (Prefs.visReadsCanvasZoom > 8)
			g.setStroke(new BasicStroke(2));

		// If the reads are on the row, draw a line connecting them
		if(lineIndex == mateLineIndex && Prefs.visPaired)
			renderLinkLine(ntH, ntW, offset, g);

		g.setColor(TabletUtils.red1);

		// Draw outlines around the reads in the pair
		renderReadOutline(readA, lineIndex, ntH, ntW, offset, g);
		renderReadOutline(readB, mateLineIndex, ntH, ntW, offset, g);

		g.setStroke(oldStroke);
	}

	private void renderLinkLine(int ntH, int ntW, int offset, Graphics2D g)
	{
		int y = (lineIndex * ntH) + (ntH / 2);
		int xS;
		int xE;
		if (readA.getStartPosition() < readB.getStartPosition())
		{
			xS = (readA.getEndPosition() * ntW) + offset + ntW;
			xE = (readB.getStartPosition() * ntW) + offset;
		}
		else
		{
			xS = (readB.getEndPosition() * ntW) + offset + ntW;
			xE = (readA.getStartPosition() * ntW) + offset;
		}
		g.setColor(Color.BLACK);
		g.drawLine(xS, y, xE, y);
	}

	private void renderReadOutline(Read read, int line, int ntH, int ntW, int offset, Graphics2D g)
	{
		int y  = line * ntH;
		int xS = (read.getStartPosition() * ntW) + offset;
		int xE = (read.getEndPosition() * ntW) + ntW + offset;

		g.drawRect(xS, y, xE-xS-1, ntH-1);
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
		s = e = columnIndex;
		
		if(readA.getStartPosition() < readB.getStartPosition())
		{
			s = readA.getStartPosition();
			e = readB.getEndPosition();
		}
		else
		{
			s = readB.getStartPosition();
			e = readA.getEndPosition();
		}

		return (columnIndex >= s && columnIndex <= e ? true : false);
	}

	public void setMateLineIndex(int mateLineIndex)
		{ this.mateLineIndex = mateLineIndex; }

	public void setColumnIndex(int columnIndex)
		{ this.columnIndex = columnIndex; }
}