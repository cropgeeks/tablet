package tablet.gui.viewer;

import java.awt.Color;
import java.awt.Graphics2D;

public class SubsetOverlayer implements IOverlayRenderer
{
	private ReadsCanvas rCanvas;

	public SubsetOverlayer(ReadsCanvas rCanvas)
	{
		this.rCanvas = rCanvas;
	}

	public void render(Graphics2D g)
	{
		g.setPaint(new Color(255, 255, 255, 200));
		g.fillRect(rCanvas.pX1, rCanvas.pY1, rCanvas.pX2-rCanvas.pX1+1, rCanvas.pY2);
	}
}
