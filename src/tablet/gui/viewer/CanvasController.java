// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
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

	private JScrollBar hBar, vBar;
	private JViewport viewport;

	CanvasController(AssemblyPanel aPanel, JScrollPane sp)
	{
		this.aPanel = aPanel;

		readsCanvas = aPanel.readsCanvas;
		consensusCanvas = aPanel.consensusCanvas;
		proteinCanvas = aPanel.proteinCanvas;
		scaleCanvas = aPanel.scaleCanvas;

		viewport = sp.getViewport();
		viewport.addChangeListener(this);
		hBar = sp.getHorizontalScrollBar();
		vBar = sp.getVerticalScrollBar();

		readsCanvas.viewport = viewport;
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
	}

	private void computePanelSizes()
	{
		int zoom = Prefs.visReadsZoomLevel;

		readsCanvas.setDimensions(zoom, zoom);
		consensusCanvas.setDimensions();
		proteinCanvas.setDimensions();

		// Once the canvas knows how big it needs to be, adjust the scrollbars
		// so that they work on the same canvas size
		int minSize = (int) (readsCanvas.ntW >= 1 ? readsCanvas.ntW : 1);
		setScrollbarAdjustmentValues(minSize, readsCanvas.ntH);

		// Deals with (rare) occasions where zooming in from a non-scrollbarred
		// canvas to one that needs scrollbars wouldn't make them appear
//		sp.revalidate();

		// This call is needed to force the canvas to recalculate its px1,px2
		// values which might not update if the new data doesn't cause a scroll-
		// bar event. If they don't update, the scale canvas won't be updated
		stateChanged(null);
	}

	void moveToLater(final int rowIndex, final int colIndex, final boolean centre)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				moveTo(rowIndex, colIndex, centre);
			}
		});
	}

	void moveToNow(int rowIndex, int colIndex, boolean centre)
	{
		moveTo(rowIndex, colIndex, centre);
	}

	// Jumps to a position relative to the given row and column
	private void moveTo(int rowIndex, int colIndex, boolean centre)
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
				offset = (int)(((readsCanvas.ntOnScreenX * readsCanvas.ntW) / 2) - readsCanvas.ntW);

			int x = (int)(colIndex * readsCanvas.ntW - offset);
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
		computePanelSizes();
	}

	void clickZoom(MouseEvent e)
	{
		readsCanvas.pCenter = new Point(e.getX(), e.getY());

		BandAdjust.zoomIn(3);
	}

	// Jumps the screen left by one "page"
	public void pageLeft()
	{
		int jumpTo = scaleCanvas.ntL - (readsCanvas.ntOnScreenX);// - readsCanvas.offset;
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