package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.data.*;
import tablet.gui.*;

class ReadsCanvasML extends MouseInputAdapter
{
	private AssemblyPanel aPanel;
	private ReadsCanvas rCanvas;
	private ScaleCanvas sCanvas;

	// Deals with navigation issues
	private NavigationHandler nHandler = new NavigationHandler();

	private ReadOutliner readOutliner = new ReadOutliner();

	ReadsCanvasML(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;
		rCanvas = aPanel.readsCanvas;
		sCanvas = aPanel.scaleCanvas;

		rCanvas.addMouseListener(this);
		rCanvas.addMouseMotionListener(this);
		rCanvas.overlays.add(readOutliner);
	}

	public void mouseExited(MouseEvent e)
	{
		readOutliner.read = null;

		aPanel.statusPanel.setLabels(null, null, null);
		sCanvas.setMouseBase(null);
		rCanvas.repaint();
	}

	public void mouseClicked(MouseEvent e)
	{
		if (e.getClickCount() == 2)
		{
			// Toggle the layout type
			if (Prefs.visReadLayout == 1)
				Prefs.visReadLayout = 2;
			else if (Prefs.visReadLayout == 2)
				Prefs.visReadLayout = 1;

			// Force the panel to update and redraw
			aPanel.setContig(rCanvas.contig);
		}
	}

	public void mousePressed(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
			nHandler.mousePressed(e);
	}

	public void mouseReleased(MouseEvent e)
	{
		nHandler.mouseReleased(e);
	}

	public void mouseDragged(MouseEvent e)
	{
		nHandler.mouseDragged(e);
	}

	public void mouseMoved(MouseEvent e)
	{
		if (rCanvas.contig == null)
			return;

		int xIndex = (e.getX() / rCanvas.ntW) - rCanvas.offset;
		int yIndex = (e.getY() / rCanvas.ntH);

		sCanvas.setMouseBase(xIndex);

		// Track the previously outlined read
		Read readOld = readOutliner.read;
		// And find the new one
		Read readNew = rCanvas.reads.getReadAt(yIndex, xIndex);

		readOutliner.setRead(readNew, yIndex);

		// Only repaint if the new one is not the same as the old one
		if (readOld != readNew)
			aPanel.repaint();
	}

	/** Inner class to handle navigation mouse events (dragging the canvas etc). */
	private class NavigationHandler
	{
		private Point dragPoint;

		void mousePressed(MouseEvent e)
		{
			dragPoint = e.getPoint();
		}

		void mouseReleased(MouseEvent e)
		{
			// Reset any dragging variables
			dragPoint = null;
			rCanvas.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}

		void mouseDragged(MouseEvent e)
		{
			// Dragging the canvas...
			if (dragPoint != null)
			{
				rCanvas.setCursor(new Cursor(Cursor.HAND_CURSOR));

				int diffX = dragPoint.x - e.getPoint().x;
				int diffY = dragPoint.y - e.getPoint().y;

				aPanel.moveBy(diffX, diffY);
			}
		}
	}

	// Inner class to draw an outline around a specified read.
	private class ReadOutliner implements IOverlayRenderer
	{
		private NumberFormat nf = NumberFormat.getInstance();

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
				ReadMetaData data = aPanel.getAssembly().getReadMetaData(read);

				// Start and ending positions (against consensus)
				readS = read.getStartPosition();
				readE = read.getEndPosition();
				int length = (readE-readS+1);

				// Name
				String readName = data.getName();
				// Formatted C/U plus start and end
				String label2 = (data.isComplemented() ? "C: " : "U: ")
					+ nf.format(readS+1) + " - " + nf.format(readE+1)
					+ " (length: " + nf.format(length) + ")";

				aPanel.statusPanel.setLabels(readName, label2, null);
				rCanvas.setToolTipText(readName);
			}
			else
			{
				aPanel.statusPanel.setLabels(null, null, null);
				rCanvas.setToolTipText(null);
			}
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