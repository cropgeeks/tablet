package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.data.*;
import tablet.gui.*;

import scri.commons.gui.*;

class ReadsCanvasML extends MouseInputAdapter
{
	private boolean isOSX = SystemUtils.isMacOS();

	private AssemblyPanel aPanel;
	private ReadsCanvas rCanvas;
	private ScaleCanvas sCanvas;

	// Deals with navigation issues
	private NavigationHandler nHandler = new NavigationHandler();
	private NavigationHighlighter nHighlighter;

	// Deals with pop-up menus
	private ReadsCanvasMenu rCanvasMenu;

	private ReadsCanvasInfoPane infoPane = new ReadsCanvasInfoPane();
	private ReadOutliner readOutliner = new ReadOutliner();

	ReadsCanvasML(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;
		rCanvas = aPanel.readsCanvas;
		sCanvas = aPanel.scaleCanvas;

		// Create the various objects that track the mouse
		rCanvasMenu = new ReadsCanvasMenu(aPanel, infoPane);
		nHighlighter = new NavigationHighlighter(aPanel, infoPane);
		infoPane.setCanvases(sCanvas, rCanvas);

		// Then add listeners and overlays to the canvas
		rCanvas.addMouseListener(this);
		rCanvas.addMouseMotionListener(this);
		rCanvas.overlays.add(readOutliner);
		rCanvas.overlays.add(nHighlighter);
		rCanvas.overlays.add(infoPane);
	}

	private boolean isMetaClick(MouseEvent e)
	{
		return isOSX && e.isMetaDown() || !isOSX && e.isControlDown();
	}

	public void mouseExited(MouseEvent e)
	{
		readOutliner.read = null;
		infoPane.setMousePosition(null);
		nHighlighter.setMousePosition(null);

		sCanvas.setMouseBase(null);
		rCanvas.repaint();
	}

	public void mouseClicked(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
		{
			if (e.getClickCount() == 2)
				aPanel.clickZoom(e);

			// Page left or right if the navigation arrows were clicked on
			else if (nHighlighter.isLeftActive())
				aPanel.pageLeft();
			else if (nHighlighter.isRightActive())
				aPanel.pageRight();
		}
	}

	public void mousePressed(MouseEvent e)
	{
		trackMouse(e);

		if (e.isPopupTrigger())
			rCanvasMenu.handlePopup(e);

		if (SwingUtilities.isLeftMouseButton(e))
			nHandler.mousePressed(e);
	}

	public void mouseReleased(MouseEvent e)
	{
		trackMouse(e);

		if (e.isPopupTrigger())
			rCanvasMenu.handlePopup(e);

		nHandler.mouseReleased(e);
	}

	public void mouseDragged(MouseEvent e)
	{
		nHandler.mouseDragged(e);
	}

	public void mouseMoved(MouseEvent e)
	{
		if (rCanvasMenu.isShowingMenu() == false)
			trackMouse(e);
	}

	private void trackMouse(MouseEvent e)
	{
		int xIndex = (e.getX() / rCanvas.ntW) - rCanvas.offset;
		int yIndex = (e.getY() / rCanvas.ntH);

		// Track the mouse position
		sCanvas.setMouseBase(xIndex);
		infoPane.setMousePosition(e.getPoint());
		nHighlighter.setMousePosition(e.getPoint());

		// Track the read under the mouse (if any)
		Read read = rCanvas.reads.getReadAt(yIndex, xIndex);
		readOutliner.setRead(read, xIndex, yIndex);

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
		Read read;

		int readS, readE;
		int lineIndex;

		void setRead(Read read, int colIndex, int lineIndex)
		{
			this.read = read;
			this.lineIndex = lineIndex;

			if (read != null)
			{
				ReadMetaData data = aPanel.getAssembly().getReadMetaData(read);

				// Start and ending positions (against consensus)
				readS = read.getStartPosition();
				readE = read.getEndPosition();

				infoPane.setData(lineIndex, read, data);
			}
			else
				infoPane.setMousePosition(null);
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