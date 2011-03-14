// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;

import tablet.data.*;
import tablet.gui.*;

/**
 * Master InfoPaneRenderer class, handles positioning and drawing of both
 * ReadsCanvasInfoPane and ReadCanvasPairInfoPane.
 */
public class ReadsCanvasInfoPaneRenderer implements IOverlayRenderer
{
	ReadsCanvasInfoPane readInfo = new ReadsCanvasInfoPane();
	ReadsCanvasPairInfoPane pairInfo = new ReadsCanvasPairInfoPane();
	private int x, y;
	private Point mouse;

	public void render(Graphics2D g)
	{
		if (mouse == null || Prefs.visInfoPaneActive == false || !readInfo.isOverRead())
			return;

		int h = readInfo.h;
		int w = readInfo.w;
		boolean mateMapped = Assembly.getReadMetaData(readInfo.read, false).getMateMapped();

		// Make adjustments to ensure info panes will never be drawn off screen
		if(readInfo.read instanceof MatedRead)
		{
			if(pairInfo.read != null)
				calculatePosition(h*2);
			else
				calculatePosition(h+readInfo.basicHeight);
		}
		else
			calculatePosition(h);

		g.translate(x, y);

		readInfo.drawBox(g);

		if(mateMapped)
		{
			g.drawLine(w/2, h, w/2, h+10);

			g.translate(0, h+10);
			pairInfo.draw(g);
		}

		g.translate(-x, -y);
	}

	protected void calculatePosition(int height)
	{
		// Decide where to draw (roughly)
		x = mouse.x + 15;
		y = mouse.y + 20;

		ReadsCanvas rCanvas = readInfo.rCanvas;
		int w = readInfo.w;
		int pX2Max = rCanvas.pX2Max;
		int pY2 = rCanvas.pY2;

		// Then adjust if the box would be offscreen to the right or bottom
		if (x + w >=pX2Max)
			x = pX2Max - w - 1;
		if (y + height >= pY2)
			y = pY2 - height - 1;
	}

	public void setMousePosition(Point mouse)
	{
		this.mouse = mouse;
		readInfo.setMousePosition(mouse);
		pairInfo.setMousePosition(mouse);
	}
}