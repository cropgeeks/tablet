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

		aPanel.statusPanel.setLabels(null, null, null);
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

		readOutliner.setRead(readNew, yIndex);

		// Only repaint if the new one is not the same as the old one
		if (readOld != readNew)
			aPanel.repaint();
	}

	// Simple class to draw an outline around a specified read.
	private class ReadOutliner implements IOverlayRenderer
	{
		Read read;
		String readName;

		int readS, readE;
		int lineIndex;

		void setRead(Read read, int lineIndex)
		{
			this.read = read;
			this.lineIndex = lineIndex;

			if (read != null)
			{
				readS = read.getStartPosition();
				readE = read.getEndPosition();

				readName = aPanel.getAssembly().getReadName(read);
				aPanel.statusPanel.setLabels(readName, (readS+1) + "-" + (readE+1), null);
			}
			else
				aPanel.statusPanel.setLabels(null, null, null);
		}

		public void render(Graphics2D g)
		{
			if (read == null)
				return;

			int offset = rCanvas.offset * rCanvas.ntW;

			int y  = lineIndex * rCanvas.ntH;
			int xS = readS * rCanvas.ntW + offset;
			int xE = readE * rCanvas.ntW + rCanvas.ntW + offset;

			g.setColor(Color.red);
			g.drawRect(xS, y, xE-xS-1, rCanvas.ntH-1);
		}
	}
}