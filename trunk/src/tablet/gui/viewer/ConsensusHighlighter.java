// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.Color;
import java.awt.Graphics2D;

public class ConsensusHighlighter extends Thread implements IOverlayRenderer
{
	private static ConsensusHighlighter previous;

	private ConsensusCanvas cCanvas;
	private ReadsCanvas rCanvas;

	private boolean isOK = true;
	private int alphaEffect = 0;
	
	private int position;
	private int length;


	public ConsensusHighlighter(AssemblyPanel aPanel, int position, int length)
	{
		this.cCanvas = aPanel.consensusCanvas;
		this.rCanvas = aPanel.readsCanvas;
		this.position = position;
		this.length = length;

		if (previous != null)
			previous.interrupt();
		previous = this;

		start();
	}

	public void run()
	{
		cCanvas.overlays.addFirst(this);

		// Darken the regions around the line/marker
		alphaEffect = 200;
		cCanvas.repaint();

		// Then wait for a while
		try { Thread.sleep(5000); }
		catch (InterruptedException e) { isOK = false; }

		// Before fading the other lines back to normality
		for (int i = 1; i <= 40 && isOK; i++)
		{
			// 40 * 5 = 200 (the starting alpha)
			alphaEffect = (200 - (i * 5));
			cCanvas.repaint();

			// 25 * 40 = 1000 (1 second)
			try { Thread.sleep(25); }
			catch (InterruptedException e) { isOK = false; }
		}

		cCanvas.overlays.remove(this);
		cCanvas.repaint();
	}

	public void render(Graphics2D g)
	{
		int offset = rCanvas.offset;
		int ntW = rCanvas.ntW;

		g.setPaint(new Color(20, 20, 20, alphaEffect));

		// Top-left corner of the read
		int x1 = (position - offset);
		int y1 = 0;
		// Bottom-right corner of the read
		int x2 = position + length - offset;
		int y2 = rCanvas.ntH;

		int width = (cCanvas.width+cCanvas.x1-x2)*ntW;

		// Fill in the four boxes that fir around the read:
		//  above it, to the left and right of it (same height) and below it
		g.fillRect(0, y1, x1*ntW, y2);
		g.fillRect(x2*ntW, 0, width, y2);
	}

}