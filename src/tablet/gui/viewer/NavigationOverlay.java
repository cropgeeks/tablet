// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

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

	private Image navLBlu, navRBlu;
	private Image navLGrn, navRGrn;
	private int imgW, imgH;

	private Point mouse;
	private boolean isLeftActive;
	private boolean isRightActive;

	public NavigationOverlay(AssemblyPanel aPanel, ReadsCanvasInfoPane infoPane)
	{
		this.infoPane = infoPane;
		rCanvas = aPanel.readsCanvas;

		navLBlu = Icons.getIcon("NAVLBLU32").getImage();
		navRBlu = Icons.getIcon("NAVRBLU32").getImage();
		navLGrn = Icons.getIcon("NAVLGRN32").getImage();
		navRGrn = Icons.getIcon("NAVRGRN32").getImage();

		// Assumption being made that the images are all the same size...
		imgW = navLBlu.getWidth(null);
		imgH = navLBlu.getHeight(null);
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

			infoPane.setMousePosition(null, -1, -1);
			isLeftActive = true;
		}

		// Extra check: are we at the far left hand side of the view?
		if (rCanvas.pX1 == 0)
		{
			// If we are...perhaps there's still more data (not yet loaded)
			if (rCanvas.contig.getDataStart() < rCanvas.contig.getVisualStart())
				g.drawImage(navLGrn, ix, iy, null);
			else
				isLeftActive = false;
		}
		else
			g.drawImage(navLBlu, ix, iy, null);

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

			infoPane.setMousePosition(null, -1, -1);
			isRightActive = true;
		}

		// Extra check: are we at the far right hand side of the view?
		if (rCanvas.pX2 == rCanvas.canvasW-1)
		{
			// If we are...perhaps there's still more data (not yet loaded)
			if (rCanvas.contig.getDataEnd() > rCanvas.contig.getVisualEnd())
				g.drawImage(navRGrn, ix, iy, null);
			else
				isRightActive = false;
		}
		else
			g.drawImage(navRBlu, ix, iy, null);

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