// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;

import tablet.data.*;

/**
 * Overlay renderer used for highlighting a specific read
 */
public class ReadHighlighter extends AlphaOverlay
{
	private int lineIndex;
	private int start;
	private int end;

	public ReadHighlighter(AssemblyPanel aPanel, Read read, int lineIndex)
	{
		super(aPanel);

		this.lineIndex = lineIndex;

		start = read.getStartPosition() - rCanvas.offset;
		end = read.getEndPosition() - rCanvas.offset;

		start();
	}

	/**
	 * Constructor used to highlight a subsequence of a read.
	 *
	 * @param aPanel	The current assembly panel.
	 * @param lineIndex	The line (y) index of the subsequence to be highlighted.
	 * @param start	The starting index of the subsequence.
	 * @param end	The end index of the subsequence.
	 */
	public ReadHighlighter(AssemblyPanel aPanel, int lineIndex, int start, int end)
	{
		super(aPanel);

		this.lineIndex = lineIndex;

		this.start = start - rCanvas.offset;
		this.end = end - rCanvas.offset;

		start();
	}

	public void render(Graphics2D g)
	{
		aPanel.overviewCanvas.updateRead(lineIndex, start, end);

		g.setPaint(new Color(20, 20, 20, alphaEffect));

		// Top-left corner of the read
		int x1 = start * rCanvas.ntW;
		int y1 = lineIndex * rCanvas.ntH;
		// Bottom-right corner of the read
		int x2 = end * rCanvas.ntW + rCanvas.ntW;
		int y2 = y1 + rCanvas.ntH;

		// Fill in the four boxes that fir around the read:
		//  above it, to the left and right of it (same height) and below it
		g.fillRect(0, 0, rCanvas.pX2Max+1, y1);
		g.fillRect(0, y1, x1, rCanvas.ntH);
		g.fillRect(x2, y1, rCanvas.pX2Max-x2+1, rCanvas.ntH);
		g.fillRect(0, y2, rCanvas.pX2Max+1, rCanvas.pY2-y2+1);
	}
}