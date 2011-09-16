package tablet.gui.viewer;

import java.awt.*;
import tablet.data.*;
import tablet.gui.*;

public class PairOutlinerOverlay implements IOverlayRenderer
{
	private ReadsCanvasInfoPane infoPane;
	private ReadsCanvas rCanvas;

	private Read readA, readB;
	private int lineIndex, mateLineIndex, columnIndex;

	PairOutlinerOverlay(ReadsCanvas rCanvas, ReadsCanvasInfoPane infoPane)
	{
		this.infoPane = infoPane;
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
			infoPane.setData(lineIndex, readA, data, false);
		}
	}

	public void render(Graphics2D g)
	{
		// Remember the current stroke so it can be reset afterwards
		Stroke oldStroke = g.getStroke();

		// Set the outline width based on the zoom level
		if (Prefs.visReadsCanvasZoom > 18)
			g.setStroke(new BasicStroke(3));
		else if (Prefs.visReadsCanvasZoom > 8)
			g.setStroke(new BasicStroke(2));

		if(readA == null || readB == null || !isValidOutline())
			return;

		int offset = -rCanvas.offset * rCanvas.ntW;

		// If the reads are on the row, draw a line connecting them
		if(lineIndex == mateLineIndex && Prefs.visPaired)
		{
			int y = lineIndex * rCanvas.ntH + rCanvas.ntH / 2;
			int xS, xE;
			if(readA.getStartPosition() < readB.getStartPosition())
			{
				xS = readA.getEndPosition() * rCanvas.ntW + offset + rCanvas.ntW;
				xE = readB.getStartPosition() * rCanvas.ntW + offset;
			}
			else
			{
				xS = readB.getEndPosition() * rCanvas.ntW + offset + rCanvas.ntW;
				xE = readA.getStartPosition() * rCanvas.ntW + offset;
			}

			g.setColor(Color.BLACK);
			g.drawLine(xS, y, xE, y);
		}

		// Draw outlines around the reads in the pair
		int y  = lineIndex * rCanvas.ntH;
		int xS = readA.getStartPosition() * rCanvas.ntW + offset;
		int xE = readA.getEndPosition() * rCanvas.ntW + rCanvas.ntW + offset;

		g.setColor(TabletUtils.red1);
		g.drawRect(xS, y, xE-xS-1, rCanvas.ntH-1);

		y = mateLineIndex * rCanvas.ntH;
		xS = readB.getStartPosition() * rCanvas.ntW + offset;
		xE = readB.getEndPosition() * rCanvas.ntW + rCanvas.ntW + offset;

		g.setColor(TabletUtils.red1);
		g.drawRect(xS, y, xE-xS-1, rCanvas.ntH-1);

		g.setStroke(oldStroke);
	}


	/**
	 * Check if the mouse coordinates are between the start of the left read and
	 * the end of the right read, returning true if they are.
	 */
	public boolean isValidOutline()
	{
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

		return (columnIndex < s || columnIndex > e ? false : true);
	}

	public void setMateLineIndex(int mateLineIndex)
		{ this.mateLineIndex = mateLineIndex; }

	public void setColumnIndex(int columnIndex)
		{ this.columnIndex = columnIndex; }
}