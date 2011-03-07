// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

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

	private ReadsCanvasInfoPaneRenderer infoPaneRenderer = new ReadsCanvasInfoPaneRenderer();
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
		rCanvasMenu = new ReadsCanvasMenu(aPanel, infoPaneRenderer);
		nOverlay = new NavigationOverlay(aPanel, infoPaneRenderer);
		outliner = new OutlinerOverlay(aPanel, infoPaneRenderer);
		pairOutliner = new PairOutlinerOverlay(rCanvas, infoPaneRenderer);
		infoPaneRenderer.readInfo.setAssemblyPanel(aPanel);
		infoPaneRenderer.pairInfo.setAssemblyPanel(aPanel);

		// Then add listeners and overlays to the canvas
		rCanvas.addMouseListener(this);
		rCanvas.addMouseMotionListener(this);
		rCanvas.addMouseWheelListener(this);

		rCanvas.overlays.add(new ReadShadower(aPanel));
		rCanvas.overlays.add(outliner);
		rCanvas.overlays.add(pairOutliner);
		rCanvas.overlays.add(nOverlay);
		rCanvas.overlays.add(infoPaneRenderer);

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
		infoPaneRenderer.setMousePosition(null);
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
			JScrollBar sBar = null;
			if (aPanel.vBar.isVisible())
				sBar = aPanel.vBar;
			else if (aPanel.hBar.isVisible())
				sBar = aPanel.hBar;

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
		int xIndex = (e.getX() / rCanvas.ntW) + rCanvas.offset;
		int yIndex = (e.getY() / rCanvas.ntH);

		// Track the mouse position
		sCanvas.setMouseBase(xIndex);
		infoPaneRenderer.setMousePosition(e.getPoint());
		nOverlay.setMousePosition(e.getPoint());

		pairOutliner.setColumnIndex(xIndex);
		ReadShadower.setMouseBase(xIndex);

		// Track the read under the mouse (if any)
		Read read = rCanvas.reads.getReadAt(yIndex, xIndex);
		outliner.setRead(read, xIndex, yIndex);
		attemptPairOutline(yIndex, xIndex);

		aPanel.repaint();
	}

	/**
	 * If we can outline a pair of reads, do so.
	 */
	private void attemptPairOutline(int yIndex, int xIndex)
	{
		if(rCanvas.reads instanceof PairedStack)
		{
			PairedStack pairedStack = (PairedStack)rCanvas.reads;
			Read[] pair = pairedStack.getPairAtLine(yIndex, xIndex);

			setupPairOutline(pair, yIndex, xIndex);
		}
		else if(rCanvas.reads instanceof Pack)
		{
			Pack pack = (Pack)rCanvas.reads;
			Read[] pair = pack.getPairAtLine(yIndex, xIndex);

			setupPairOutline(pair, yIndex, xIndex);
		}
		else if(rCanvas.reads instanceof Stack)
		{
			Stack stack = (Stack)rCanvas.reads;
			Read read = stack.getReadAt(yIndex, xIndex);
			Read mate = null;

			if(read instanceof MatedRead)
			{
				MatedRead mr = (MatedRead)read;
				if(mr.getPair() != null)
					mate = mr.getPair();
				else
					pairOutliner.setPair(null, null, 0, 0);
			}

			if (mate != null)
			{
				Read[] pair = new Read[] {read, mate};

				setupPairOutline(pair, yIndex, xIndex);
			}

		}
		else
			pairOutliner.setPair(null, null, 0, 0);
	}

	private void setupPairOutline(Read[] pair, int yIndex, int xIndex)
	{
		if (pair == null || pair[0] == null || pair[1] == null)
			pairOutliner.setPair(null, null, 0, 0);
		else
		{
			pair = orientPairCorrectly(pair);
			outlinePair(pair);
		}
	}

	private Read[] orientPairCorrectly(Read[] pair)
	{
		int s;
		int e;
		if (pair[0].getStartPosition() < pair[1].getStartPosition())
		{
			s = pair[0].getEndPosition();
			e = pair[1].getStartPosition();
		}
		else
		{
			s = pair[1].getEndPosition();
			e = pair[0].getStartPosition();
		}

		return pair;
	}

	/**
	 * Passes the pairOutliner object the pair of reads which needs to be outlined.
	 */
	private void outlinePair(Read[] pair)
	{
		if(pair == null || (pair[0] == null || pair[1] == null))
			return;

		int lineIndex = 0;
		int mateLineIndex = 0;

		lineIndex = aPanel.readsCanvas.reads.getLineForRead(pair[0]);
		mateLineIndex = aPanel.readsCanvas.reads.getLineForRead(pair[1]);

		pairOutliner.setPair(pair[0], pair[1], lineIndex, mateLineIndex);
	}
}