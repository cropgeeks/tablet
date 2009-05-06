package av.gui.viewer;

import java.awt.*;

/**
 * Interface that defines methods for classes that will perform further (post)
 * main rendering operations on the canvas. That is, what they draw will
 * basically be an overlay on top of the original canvas.
 */
interface IOverlayRenderer
{
	public void render(Graphics2D g);
}