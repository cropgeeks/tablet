// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;

import scri.commons.gui.*;

/**
 * Overlay renderer used for the navigation buttons at the edges
 */
class NavigationOverlay implements IOverlayRenderer
{
	private ReadsCanvas rCanvas;
	private ReadsCanvasInfoPane infoPane;

	private Image navLeft, navRight;
	private int imgW, imgH;

	private Point mouse;
	private boolean isLeftActive;
	private boolean isRightActive;

	public NavigationOverlay(AssemblyPanel aPanel, ReadsCanvasInfoPane infoPane)
	{
		this.infoPane = infoPane;
		rCanvas = aPanel.readsCanvas;

		navLeft = Icons.getIcon("NAVLEFT32").getImage();
		navRight = Icons.getIcon("NAVRIGHT32").getImage();

		// Assumption being made that the two images are the same size...
		imgW = navLeft.getWidth(null);
		imgH = navRight.getHeight(null);
	}

	void setMousePosition(Point mouse)
		{ this.mouse = mouse; }

	public void render(Graphics2D g)
	{
		Composite c = g.getComposite();

		isLeftActive = isRightActive = false;

		// LEFT HAND NAV CONTROL...
		// Determine where to draw the image
		int ix = rCanvas.pX1 + 10;
		int iy = rCanvas.pY2 - imgH - 20;

		// If the mouse *isn't* over the image, draw transparently
		if (mouse == null || mouse.x > ix+imgW || mouse.x < ix || mouse.y > iy+imgH || mouse.y < iy)
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP , 0.5f));
		else
		{
			String tt = RB.format(
				"gui.viewer.NavigationHighlighter.leftTooltip",
				rCanvas.ntOnScreenX);
			rCanvas.setToolTipText(tt);

			infoPane.setMousePosition(null);
			isLeftActive = true;
		}

		// Extra check: are we at the far left hand side of the view?
		if (rCanvas.pX1 == 0)
			isLeftActive = false;
		else
			g.drawImage(navLeft, ix, iy, null);

		g.setComposite(c);

		// RIGHT HAND NAV CONTROL...
		// Determine where to draw the image
		ix = rCanvas.pX2Max - imgW - 10;

		// If the mouse *isn't* over the image, draw transparently
		if (mouse == null || mouse.x < ix || mouse.x > ix+imgW || mouse.y > iy+imgH || mouse.y < iy)
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP , 0.5f));
		else
		{
			String tt = RB.format(
				"gui.viewer.NavigationHighlighter.rightTooltip",
				rCanvas.ntOnScreenX);
			rCanvas.setToolTipText(tt);

			infoPane.setMousePosition(null);
			isRightActive = true;
		}

		// Extra check: are we at the far right hand side of the view?
		if (rCanvas.pX2 == rCanvas.canvasW-1)
			isRightActive = false;
		else
			g.drawImage(navRight, ix, iy, null);

		g.setComposite(c);

		// Disable the tooltip if neither link is active
		if (!isLeftActive && !isRightActive)
			rCanvas.setToolTipText(null);
	}

	boolean isLeftActive()
		{ return isLeftActive; }

	boolean isRightActive()
		{ return isRightActive; }
}