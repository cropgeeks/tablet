// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

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

		int x1 = (start - rCanvas.offset) * rCanvas.ntW;
		int x2 = x1 + (end-start+1) * rCanvas.ntW;

		g.fillRect(0, 0, x1, rCanvas.pY2+1);
		g.fillRect(x2, 0, rCanvas.pX2Max, rCanvas.pY2+1);
	}
}