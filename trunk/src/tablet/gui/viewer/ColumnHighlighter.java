package tablet.gui.viewer;

import java.awt.*;

/**
 * Overlay renderer used for picking out specific columns
 */
public class ColumnHighlighter extends AlphaOverlay
{
	private int index = 0;

	public ColumnHighlighter(AssemblyPanel aPanel, int index)
	{
		super(aPanel);

		this.index = index;

		start();
	}

	public void render(Graphics2D g)
	{
		g.setPaint(new Color(20, 20, 20, alphaEffect));

		int x1 = index * rCanvas.ntW;
		int x2 = x1 + rCanvas.ntW;

		g.fillRect(0, 0, x1, rCanvas.pY2);
		g.fillRect(x2, 0, rCanvas.pX2Max, rCanvas.pY2);
	}
}