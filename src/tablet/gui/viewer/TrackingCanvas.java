// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Abstract base class for all canvases that are fixed width, but look like they
 * are tracking the ReadsCanvas (ScaleCanvas, ConsensusCanvas, etc).
 */
abstract class TrackingCanvas extends JPanel
{
	private static int TRACK_OFFSET = 3;

	protected ReadsCanvas rCanvas;

	// The LHS offset (difference) between the left-most read and the consensus
	int offset;

	// Tracks the left most and right most bases being displayed
	int ntL, ntR;

	// Left and right pixel positions
	int x1, x2;
	// And width
	int width;

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;

		// Determine lhs and rhs of canvas
		x1 = rCanvas.pX1;
		x2 = rCanvas.pX2;
		width = (x2-x1+1);

		// Clip to only draw what's needed (mainly ignoring what would appear
		// above the vertical scrollbar of the reads canvas)
		g.setClip(TRACK_OFFSET, 0, width, getHeight());
		g.translate(TRACK_OFFSET-x1, 0);
	}

	protected int getMouseX(MouseEvent e)
	{
		return e.getX() - TRACK_OFFSET;
	}
}