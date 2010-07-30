// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.data.*;

class ReadsCanvasML extends MouseInputAdapter
{
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
	private PairOutliner pairOutliner;

	ReadsCanvasML(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;
		rCanvas = aPanel.readsCanvas;
		sCanvas = aPanel.scaleCanvas;

		// Create the various objects that track the mouse
		rCanvasMenu = new ReadsCanvasMenu(aPanel, infoPane);
		nOverlay = new NavigationOverlay(aPanel, infoPane);
		outliner = new OutlinerOverlay(aPanel, infoPane);
		pairOutliner = new PairOutliner(rCanvas, infoPane);
		infoPane.setAssemblyPanel(aPanel);

		// Then add listeners and overlays to the canvas
		rCanvas.addMouseListener(this);
		rCanvas.addMouseMotionListener(this);
		rCanvas.overlays.add(new ReadShadower(aPanel));
		rCanvas.overlays.add(outliner);
		rCanvas.overlays.add(pairOutliner);
		rCanvas.overlays.add(nOverlay);
		rCanvas.overlays.add(infoPane);
	}

	public void mouseExited(MouseEvent e)
	{
		pairOutliner.setPair(null, null, 0, 0);
		outliner.setRead(null, 0, 0);
		infoPane.setMousePosition(null);
		nOverlay.setMousePosition(null);

		if (rCanvasMenu.isShowingMenu() == false)
			ReadShadower.setMouseBase(null);

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
		int xIndex = (e.getX() / rCanvas.ntW) + rCanvas.offset;
		int yIndex = (e.getY() / rCanvas.ntH);

		// Track the mouse position
		sCanvas.setMouseBase(xIndex);
		infoPane.setMousePosition(e.getPoint());
		nOverlay.setMousePosition(e.getPoint());

		pairOutliner.setColumnIndex(xIndex);
		ReadShadower.setMouseBase(xIndex);

		// Track the read under the mouse (if any)
		Read read = rCanvas.reads.getReadAt(yIndex, xIndex);
		outliner.setRead(read, xIndex, yIndex);
		outlinePair(yIndex, xIndex);

		aPanel.repaint();
	}

	/**
	 * If we can outline a pair of reads, do so. Delegates to outlinePair(Read [])
	 * to setup the outlining.
	 */
	private void outlinePair(int yIndex, int xIndex)
	{
		if(rCanvas.reads instanceof PairedStack)
		{
			PairedStack set = (PairedStack)rCanvas.reads;
			Read[] pair = set.getPairAtLine(yIndex, xIndex);

			if(pair == null || pair[0] == null || pair[1] == null)
				pairOutliner.setPair(null, null, 0, 0);
			else
			{
				int s, e;
				if(pair[0].getStartPosition() < pair[1].getStartPosition())
				{
					s = pair[0].getEndPosition();
					e = pair[1].getStartPosition();
				}
				else
				{
					s = pair[1].getEndPosition();
					e = pair[0].getStartPosition();
				}
				if(set.getReadAt(yIndex, xIndex) != null || s < xIndex && xIndex < e)
					outlinePair(pair);
				else
					pairOutliner.setPair(null, null, 0, 0);
			}
		}
		else if(rCanvas.reads instanceof PackSet)
		{
			PackSet set = (PackSet)rCanvas.reads;
			Read[] pair = set.getPairAtLine(yIndex, xIndex);
		
			if(pair == null || pair[0] == null || pair[1] == null)
				pairOutliner.setPair(null, null, 0, 0);
			else
			{
				int s, e;
				if(pair[0].getStartPosition() < pair[1].getStartPosition())
				{
					s = pair[0].getEndPosition();
					e = pair[1].getStartPosition();
				}
				else
				{
					s = pair[1].getEndPosition();
					e = pair[0].getStartPosition();
				}
				if(set.getReadAt(yIndex, xIndex) != null || s < xIndex && xIndex < e)
					outlinePair(pair);
				else
					pairOutliner.setPair(null, null, 0, 0);
			}
		}
		else
			pairOutliner.setPair(null, null, 0, 0);
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
}