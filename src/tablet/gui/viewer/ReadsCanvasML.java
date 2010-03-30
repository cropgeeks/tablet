// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.data.*;

import scri.commons.gui.*;

class ReadsCanvasML extends MouseInputAdapter
{
	private boolean isOSX = SystemUtils.isMacOS();

	private AssemblyPanel aPanel;
	private ReadsCanvas rCanvas;
	private ScaleCanvas sCanvas;

	// Deals with navigation issues
	private NavigationHandler nHandler = new NavigationHandler();
	private NavigationOverlay nOverlay;

	// Deals with pop-up menus
	private ReadsCanvasMenu rCanvasMenu;

	private ReadsCanvasInfoPane infoPane = new ReadsCanvasInfoPane();
	private OutlinerOverlay outliner;

	private boolean showShadower = false;

	ReadsCanvasML(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;
		rCanvas = aPanel.readsCanvas;
		sCanvas = aPanel.scaleCanvas;

		// Create the various objects that track the mouse
		rCanvasMenu = new ReadsCanvasMenu(aPanel, infoPane);
		nOverlay = new NavigationOverlay(aPanel, infoPane);
		outliner = new OutlinerOverlay(aPanel, infoPane);
		infoPane.setAssemblyPanel(aPanel);

		// Then add listeners and overlays to the canvas
		rCanvas.addMouseListener(this);
		rCanvas.addMouseMotionListener(this);
		rCanvas.overlays.add(outliner);
		rCanvas.overlays.add(nOverlay);
		rCanvas.overlays.add(infoPane);
	}

	private boolean isMetaClick(MouseEvent e)
	{
		return isOSX && e.isMetaDown() || !isOSX && e.isControlDown();
	}

	public void mouseExited(MouseEvent e)
	{
		outliner.setRead(null, 0, 0);
		infoPane.setMousePosition(null);
		nOverlay.setMousePosition(null);

		if(aPanel.readShadower != null && !showShadower)
			aPanel.readShadower.setMouseBase(null);

		sCanvas.setMouseBase(null);
		rCanvas.repaint();
	}

	public void mouseClicked(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
		{
			// Page left or right if the navigation arrows were clicked on
			if (nOverlay.isLeftActive())
				aPanel.pageLeft();
			else if (nOverlay.isRightActive())
				aPanel.pageRight();

			else if (e.getClickCount() == 2)
				aPanel.clickZoom(e);
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
		{
			rCanvasMenu.handlePopup(e);
			showShadower = true;
		}

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
		showShadower = false;
		
		int xIndex = (e.getX() / rCanvas.ntW) + rCanvas.offset;
		int yIndex = (e.getY() / rCanvas.ntH);

		// Track the mouse position
		sCanvas.setMouseBase(xIndex);
		infoPane.setMousePosition(e.getPoint());
		nOverlay.setMousePosition(e.getPoint());

		if(aPanel.readShadower != null)
			aPanel.readShadower.setMouseBase(xIndex);

		// Track the read under the mouse (if any)
		Read read = rCanvas.reads.getReadAt(yIndex, xIndex);
		outliner.setRead(read, xIndex, yIndex);

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

	ReadsCanvasMenu getRCanvasMenu()
	{
		return rCanvasMenu;
	}
}