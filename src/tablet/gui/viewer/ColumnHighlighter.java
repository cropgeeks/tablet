// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;

/**
 * Overlay renderer used for picking out specific columns
 */
public class ColumnHighlighter extends AlphaOverlay
{
	private int start, end;

	public ColumnHighlighter(AssemblyPanel aPanel, int start, int end)
	{
		super(aPanel);

		this.start = start;
		this.end = end;

		start();
	}

	public void render(Graphics2D g)
	{
		g.setPaint(new Color(20, 20, 20, alphaEffect));

		int x1 = rCanvas.getFirstRenderedPixel(start);
		// We actually want the render of the grey rectangle to start at the first
		// pixel of the base following the highlight
		int x2 = rCanvas.getFirstRenderedPixel(end+1);

		if (rCanvas.ntW < 1)
		{
			x1 = rCanvas.getFinalRenderedPixel(start);
			// As we're drawing two separate grey rectangles either side of the
			// highlighted column, when zoomed out we need to ensure there is
			// always at least one pixel between the rectangles.
			if (x2 <= x1)
				x2 = x1 + 1;
		}

		g.fillRect(0, 0, x1, rCanvas.pY2+1);
		g.fillRect(x2, 0, rCanvas.pX2Max, rCanvas.pY2+1);
	}
}