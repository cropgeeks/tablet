// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.data.*;
import tablet.gui.ribbon.*;

import scri.commons.gui.*;

class ReadsCanvasML extends MouseInputAdapter
{
	private AssemblyPanel aPanel;
	private ReadsCanvas rCanvas;
	private ScaleCanvas sCanvas;

	private NavigationOverlay nOverlay;

	// Deals with pop-up menus
	private ReadsCanvasMenu rCanvasMenu;

	private ReadsCanvasInfoPane infoPane;
	private OutlinerOverlay outliner;
	private PairOutlinerOverlay pairOutliner;

	ReadsCanvasDragHandler dragHandler;

	private boolean isOSX = SystemUtils.isMacOS();

	ReadsCanvasML(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;
		rCanvas = aPanel.readsCanvas;
		sCanvas = aPanel.scaleCanvas;

		// Create the various objects that track the mouse
		infoPane = new ReadsCanvasInfoPane(aPanel);
		rCanvasMenu = new ReadsCanvasMenu(aPanel, infoPane);
		nOverlay = new NavigationOverlay(aPanel, infoPane);
		outliner = new OutlinerOverlay(aPanel);
		pairOutliner = new PairOutlinerOverlay(rCanvas);

		// Then add listeners and overlays to the canvas
		rCanvas.addMouseListener(this);
		rCanvas.addMouseMotionListener(this);
		rCanvas.addMouseWheelListener(this);

		rCanvas.overlays.add(new ReadShadower(aPanel));
		rCanvas.overlays.add(outliner);
		rCanvas.overlays.add(pairOutliner);
		rCanvas.overlays.add(nOverlay);
		rCanvas.overlays.add(infoPane.getRenderer());

		dragHandler = new ReadsCanvasDragHandler(aPanel, rCanvas);
	}

	private boolean isMetaClick(MouseEvent e)
	{
		return isOSX && e.isMetaDown() || !isOSX && e.isControlDown();
	}

	public void mouseExited(MouseEvent e)
	{
		pairOutliner.setPair(null, null, 0, 0);
		outliner.setRead(null, 0, 0);
		infoPane.setMousePosition(null, -1, -1);
		nOverlay.setMousePosition(null);

		if (rCanvasMenu.isShowingMenu() == false)
			ReadShadower.setMouseBase(null);

		sCanvas.setMouseBase(null);
		rCanvas.repaint();
	}

	public void mouseClicked(MouseEvent e)
	{
		if (isMetaClick(e) && e.isAltDown() && e.getClickCount() == 2)
			new Breakout(rCanvas, aPanel);

		else if (SwingUtilities.isLeftMouseButton(e))
		{
			// Page left or right if the navigation arrows were clicked on
			if (nOverlay.isLeftActive())
				aPanel.getController().pageLeft();

			else if (nOverlay.isRightActive())
				aPanel.getController().pageRight();

			else if (e.getClickCount() == 2)
				aPanel.getController().clickZoom(e);
		}
	}

	public void mousePressed(MouseEvent e)
	{
		trackMouse(e);

		if (e.isPopupTrigger())
			rCanvasMenu.handlePopup(e);
	}

	public void mouseReleased(MouseEvent e)
	{
		trackMouse(e);

		if (e.isPopupTrigger())
			rCanvasMenu.handlePopup(e);
	}

	public void mouseMoved(MouseEvent e)
	{
		if (rCanvasMenu.isShowingMenu() == false)
			trackMouse(e);
	}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		int shortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		// CTRL/CMD down: do canvas zooming
		if (e.getModifiers() == shortcut)
		{
			int units = e.getWheelRotation();

			if (units > 0)
				BandAdjust.zoomIn(-units);
			else
				BandAdjust.zoomOut(units);
		}

		// Otherwise, do canvas scrolling
		else
		{
			CanvasController controller = aPanel.getController();

			JScrollBar sBar = null;
			if (controller.getVBar().isVisible())
				sBar = controller.getVBar();
			else if (controller.getHBar().isVisible())
				sBar = controller.getHBar();

			if (sBar != null)
			{
				int notches = e.getWheelRotation();
				int value = sBar.getValue();
				int units = 5 * sBar.getUnitIncrement();

				sBar.setValue(value + (notches * units));
			}
		}
	}

	private void trackMouse(MouseEvent e)
	{
		int ntIndex = rCanvas.getBaseForPixel(e.getX());
		int yIndex = (e.getY() / rCanvas.ntH);

		// DISABLED because it causes tooltip flicker as the mouse moves on and
		// off each read
		// At zoom levels <= 1, we put a spacer line between each read. This
		// detects when the mouse is in this space and therefore not over a read
//		if (rCanvas.ntH != rCanvas.readH && e.getY() % rCanvas.ntH == 2)
//			yIndex = -1;

		// Track the mouse position
		sCanvas.setMouseBase(ntIndex);
		infoPane.setMousePosition(e.getPoint(), yIndex, ntIndex);
		nOverlay.setMousePosition(e.getPoint());

		pairOutliner.setColumnIndex(ntIndex);
		ReadShadower.setMouseBase(ntIndex);

		// Track the read under the mouse (if any)
		Read read = rCanvas.reads.getReadAt(yIndex, ntIndex);
		outliner.setRead(read, ntIndex, yIndex);
		outlinePair(read, yIndex, ntIndex);


		aPanel.repaint();
	}

	/**
	 * If we can outline a pair of reads, do so.
	 */
	private void outlinePair(Read read, int yIndex, int xIndex)
	{
		pairOutliner.setPair(null, null, 0, 0);

		// If no read is under the mouse, we might be over a link line
		if (read == null)
		{
			Read[] pair = rCanvas.reads.getPairForLink(yIndex, xIndex);

			if (pair != null)
				pairOutliner.setPair(pair[0], pair[1], yIndex, yIndex);
		}

		// If a read *is* under the mouse, does it have a mate (who may also
		// be on another line too)
		else if (read instanceof MatedRead)
		{
			Read mate = ((MatedRead)read).getMate();

			if (mate != null)
			{
				int mateLineIndex = rCanvas.reads.getLineForRead(mate);
				pairOutliner.setPair(read, mate, yIndex, mateLineIndex);
			}
		}
	}
}