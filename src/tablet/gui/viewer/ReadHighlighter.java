// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

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

		start = rCanvas.getFirstRenderedPixel(read.s());
		end = rCanvas.getFirstRenderedPixel(read.e()+1);

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

		this.start = rCanvas.getFirstRenderedPixel(start);
		this.end = rCanvas.getFirstRenderedPixel(end+1);

		start();
	}

	public void render(Graphics2D g)
	{
		aPanel.overviewCanvas.updateRead(lineIndex, start, end);

		g.setPaint(new Color(20, 20, 20, alphaEffect));

		// Top-left corner of the read
		int x1 = start;
		int y1 = lineIndex * rCanvas.ntH;
		// Bottom-right corner of the read
		int x2 = end;
		int y2 = y1 + rCanvas.readH;

		// Fill in the four boxes that fir around the read:
		//  above it, to the left and right of it (same height) and below it
		g.fillRect(0, 0, rCanvas.pX2Max+1, y1);
		g.fillRect(0, y1, x1, rCanvas.readH);
		g.fillRect(x2, y1, rCanvas.pX2Max-x2+1, rCanvas.readH);
		g.fillRect(0, y2, rCanvas.pX2Max+1, rCanvas.pY2-y2+1);
	}
}