// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.data.*;

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
		rCanvas.overlays.add(new ReadShadower(aPanel));
		rCanvas.overlays.add(outliner);
		rCanvas.overlays.add(pairOutliner);
		rCanvas.overlays.add(nOverlay);
		rCanvas.overlays.add(infoPaneRenderer);

		new ReadsCanvasDragHandler(aPanel, rCanvas);
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
			PairedStack set = (PairedStack)rCanvas.reads;
			Read[] pair = set.getPairAtLine(yIndex, xIndex);

			setupPairOutline(pair, yIndex, xIndex);
		}
		else if(rCanvas.reads instanceof PackSet)
		{
			PackSet set = (PackSet)rCanvas.reads;
			Read[] pair = set.getPairAtLine(yIndex, xIndex);

			setupPairOutline(pair, yIndex, xIndex);
		}
		else if(rCanvas.reads instanceof StackSet)
		{
			StackSet set = (StackSet)rCanvas.reads;
			Read read = set.getReadAt(yIndex, xIndex);
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