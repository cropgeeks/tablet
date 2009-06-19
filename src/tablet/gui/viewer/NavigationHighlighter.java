package tablet.gui.viewer;

import java.awt.*;

import scri.commons.gui.*;

/**
 * Overlay renderer used for the navigation buttons at the edges
 */
class NavigationHighlighter implements IOverlayRenderer
{
	private AssemblyPanel aPanel;
	private ReadsCanvas rCanvas;
	private ReadsCanvasInfoPane infoPane;

	private Image navLeft, navRight;
	private int imgW, imgH;

	private Point mouse;
	private boolean isLeftActive;
	private boolean isRightActive;

	public NavigationHighlighter(AssemblyPanel aPanel, ReadsCanvasInfoPane infoPane)
	{
		this.aPanel = aPanel;
		this.infoPane = infoPane;
		rCanvas = aPanel.readsCanvas;

		navLeft = Icons.getIcon("NAVLEFT").getImage();
		navRight = Icons.getIcon("NAVRIGHT").getImage();

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
		// We want to show the control when the mouse is within 75 pixels of it
//		int lhEdge = rCanvas.pX1 + 75;

//		if (mouse.x < lhEdge)
		{
			// Determine where to draw the image
			int ix = rCanvas.pX1 + 10;
			int iy = rCanvas.pY2 - imgH - 20;

			// If the mouse *isn't* over the image, draw transparently
			if (mouse == null || mouse.x > ix+imgW || mouse.x < ix || mouse.y > iy+imgH || mouse.y < iy)
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP , 0.5f));
			else
			{
				rCanvas.setToolTipText("Page left by XXX bases");

				infoPane.setMousePosition(null);
				isLeftActive = true;
			}

			g.drawImage(navLeft, ix, iy, null);
			g.setComposite(c);
		}


		// RIGHT HAND NAV CONTROL...
//		int rhEdge = rCanvas.pX2Max - 75;

//		if (mouse.x > rhEdge)
		{
			// Determine where to draw the image
			int ix = rCanvas.pX2Max - imgW - 10;
			int iy = rCanvas.pY2 - imgH - 20;

			// If the mouse *isn't* over the image, draw transparently
			if (mouse == null || mouse.x < ix || mouse.x > ix+imgW || mouse.y > iy+imgH || mouse.y < iy)
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP , 0.5f));
			else
			{
				rCanvas.setToolTipText("Page right by XXX bases");

				infoPane.setMousePosition(null);
				isRightActive = true;
			}

			g.drawImage(navRight, ix, iy, null);
			g.setComposite(c);
		}
	}

	boolean isLeftActive()
		{ return isLeftActive; }

	boolean isRightActive()
		{ return isRightActive; }
}