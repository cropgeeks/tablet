// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

class ReadsCanvasDragHandler extends MouseInputAdapter
{
	private AssemblyPanel aPanel;
	private ReadsCanvas rCanvas;

	private Point dragPoint;

	private Cursor DEFAULT = new Cursor(Cursor.DEFAULT_CURSOR);
	private Cursor HAND = new Cursor(Cursor.HAND_CURSOR);

	ReadsCanvasDragHandler(AssemblyPanel aPanel, JComponent canvas)
	{
		this.aPanel = aPanel;
		this.rCanvas = aPanel.readsCanvas;

		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
	}

	public void mousePressed(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
			dragPoint = getPoint(e);
	}

	public void mouseReleased(MouseEvent e)
	{
		// Reset any dragging variables
		dragPoint = null;
		((JComponent)e.getSource()).setCursor(DEFAULT);
	}

	public void mouseDragged(MouseEvent e)
	{
		// Dragging the canvas...
		if (dragPoint != null)
		{
			((JComponent)e.getSource()).setCursor(HAND);

			int diffX = dragPoint.x - getPoint(e).x;
			int diffY = 0;

			if (e.getSource() instanceof ReadsCanvas)
				diffY = dragPoint.y - e.getPoint().y;

			aPanel.getController().moveBy(diffX, diffY);
		}
	}

	private Point getPoint(MouseEvent e)
	{
		if (e.getSource() instanceof ReadsCanvas)
			return e.getPoint();

		// Else for the TrackingCanvas classes...
		return new Point(rCanvas.pX1 + e.getPoint().x, e.getPoint().y);
	}
}