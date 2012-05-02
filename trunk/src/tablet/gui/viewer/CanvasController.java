// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.gui.*;
import tablet.gui.ribbon.*;

public class CanvasController extends JPanel implements ChangeListener
{
	private AssemblyPanel aPanel;
	private ConsensusCanvas consensusCanvas;
	private ProteinCanvas proteinCanvas;
	private ReadsCanvas readsCanvas;
	private ScaleCanvas scaleCanvas;

	private JScrollPane sp;
	private JScrollBar hBar, vBar;
	private JViewport viewport;

	// Normal or click zooming (affects which base to zoom in on)
	private boolean isClickZooming = false;
	// Tracks the base to zoom in on
	private float ntCenterX, ntCenterY;

	CanvasController(AssemblyPanel aPanel, JScrollPane sp)
	{
		this.aPanel = aPanel;
		this.sp = sp;

		readsCanvas = aPanel.readsCanvas;
		consensusCanvas = aPanel.consensusCanvas;
		proteinCanvas = aPanel.proteinCanvas;
		scaleCanvas = aPanel.scaleCanvas;

		viewport = sp.getViewport();
		viewport.addChangeListener(this);
		hBar = sp.getHorizontalScrollBar();
		vBar = sp.getVerticalScrollBar();
	}

	JScrollBar getHBar()
		{ return hBar; }

	JScrollBar getVBar()
		{ return vBar; }

	public void stateChanged(ChangeEvent e)
	{
		readsCanvas.computeForRedraw(viewport.getExtentSize(), viewport.getViewPosition());
	}

	private void setScrollbarAdjustmentValues(int xIncrement, int yIncrement)
	{
		hBar.setUnitIncrement(xIncrement);
		hBar.setBlockIncrement(xIncrement*25);
		vBar.setUnitIncrement(yIncrement);
		vBar.setBlockIncrement(yIncrement*25);
	}

	void forceRedraw()
	{
		readsCanvas.setContig(aPanel.getContig());

		computePanelSizes();

		// TODO: Unknown call?
		sp.revalidate();

		// This call is needed to force the canvas to recalculate its px1,px2
		// values which might not update if the new data doesn't cause a scroll-
		// bar event. If they don't update, the scale canvas won't be updated
		stateChanged(null);
	}

	private void computePanelSizes()
	{
		int zoom = Prefs.visReadsCanvasZoom;

		readsCanvas.setDimensions(zoom, zoom);
		consensusCanvas.setDimensions();
		proteinCanvas.setDimensions();

		// Once the canvas knows how big it needs to be, adjust the scrollbars
		// so that they work on the same canvas size
		int minSize = (int) (readsCanvas._ntW >= 1 ? readsCanvas._ntW : 1);
		setScrollbarAdjustmentValues(minSize, readsCanvas.ntH);
	}

	void moveToLater(final int rowIndex, final int colIndex, final boolean centre)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				moveTo(rowIndex, colIndex, centre);
			}
		});
	}

	// Jumps to a position relative to the given row and column
	void moveTo(int rowIndex, int colIndex, boolean centre)
	{
		// If 'centre' is true, offset by half the screen
		int offset = 0;

		if (rowIndex != -1)
		{
			if (centre)
				offset = ((readsCanvas.ntOnScreenY * readsCanvas.ntH) / 2) - readsCanvas.ntH;

			int y = rowIndex * readsCanvas.ntH - offset;
			vBar.setValue(y);
		}

		if (colIndex != -1)
		{
			if (centre)
				offset = (int)(((readsCanvas._ntOnScreenX * readsCanvas._ntW) / 2) - readsCanvas._ntW);

			int x = (int)(colIndex * readsCanvas._ntW - offset);
			hBar.setValue(x);
		}
	}

	// Moves the scroll bars by the given amount in the x and y directions
	void moveBy(int x, int y)
	{
		hBar.setValue(hBar.getValue() + x);
		vBar.setValue(vBar.getValue() + y);
	}

	public void doZoom()
	{
		// Track the center of the screen (before the zoom)
		if (isClickZooming == false)
		{
			ntCenterX = readsCanvas.ntCenterX;
			ntCenterY = readsCanvas.ntCenterY;
		}

		// This is needed because for some crazy reason the moveToPosition call
		// further down will not work correctly until after Swing has stopped
		// generating endless resize events that affect the scrollbars
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				moveTo(Math.round(ntCenterY), Math.round(ntCenterX), true);

			}
		});

		computePanelSizes();
	}

	void clickZoom(MouseEvent e)
	{
		isClickZooming = true;

		ntCenterX = (e.getX() / readsCanvas._ntW);
		ntCenterY = (e.getY() / readsCanvas.ntH);

		BandAdjust.zoomIn(3);

		isClickZooming = false;
	}

	// Jumps the screen left by one "page"
	public void pageLeft()
	{
		int jumpTo = scaleCanvas.ntL - (readsCanvas._ntOnScreenX);// - readsCanvas.offset;
//		moveToLater(-1, jumpTo, false);
		aPanel.moveToPosition(-1, jumpTo, false);
	}

	// Jumps the screen right by one "page"
	public void pageRight()
	{
		int jumpTo = scaleCanvas.ntR + 1;// - readsCanvas.offset;
//		moveToLater(-1, jumpTo, false);
		aPanel.moveToPosition(-1, jumpTo, false);
	}
}