package av.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

import av.data.*;

class ReadsCanvasMouseListener extends MouseInputAdapter
{
	private AssemblyPanel aPanel;
	private ReadsCanvas rCanvas;

	private ReadOutliner readOutliner = new ReadOutliner();

	ReadsCanvasMouseListener(AssemblyPanel aPanel, ReadsCanvas rCanvas)
	{
		this.aPanel = aPanel;
		this.rCanvas = rCanvas;

		rCanvas.addMouseListener(this);
		rCanvas.addMouseMotionListener(this);
		rCanvas.overlays.add(readOutliner);
	}

	public void mouseExited(MouseEvent e)
	{
		readOutliner.read = null;

		rCanvas.repaint();
	}

	public void mouseMoved(MouseEvent e)
	{
		int xIndex = (e.getX() / rCanvas.ntW) - rCanvas.offset;
		int yIndex = (e.getY() / rCanvas.ntH);

		// Track the previously outlined read
		Read readOld = readOutliner.read;
		// And find the new one
		Read readNew = rCanvas.reads.getReadAt(yIndex, xIndex);

		readOutliner.read = readNew;
		readOutliner.lineIndex = yIndex;

		// Only repaint if the new one is not the same as the old one
		if (readOld != readNew)
			aPanel.repaint();
	}

	// Simple class to draw an outline around a specified read.
	private class ReadOutliner implements IOverlayRenderer
	{
		Read read;

		int xReadS, xReadE;
		int lineIndex;

		public void render(Graphics2D g)
		{
			if (read == null)
				return;

			int xReadS = read.getStartPosition();
			int xReadE = read.getEndPosition();

			int offset = rCanvas.offset * rCanvas.ntW;

			int y  = lineIndex * rCanvas.ntH;
			int xS = xReadS * rCanvas.ntW + offset;
			int xE = xReadE * rCanvas.ntW + rCanvas.ntW + offset;

			System.out.println("START: " + xReadS);

			g.setColor(Color.red);
			g.drawRect(xS, y, xE-xS-1, rCanvas.ntH-1);
		}
	}
}