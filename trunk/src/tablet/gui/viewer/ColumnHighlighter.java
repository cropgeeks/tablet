package tablet.gui.viewer;

import java.awt.*;

/**
 * Overlay renderer used for picking out specific columns
 */
public class ColumnHighlighter extends Thread implements IOverlayRenderer
{
	private AssemblyPanel aPanel;
	private ReadsCanvas rCanvas;

	private boolean isOK = true;
	private int alphaEffect = 0;
	private int index = 0;
	private int method = 0;

	/**
	 * Constructs and runs a new highlighter. A previous instance can be passed
	 * to this object so it can ensure it has been killed before beginning the
	 * new highlighting.
	 */
	public ColumnHighlighter(AssemblyPanel aPanel, int index, ColumnHighlighter previous)
	{
		this.aPanel = aPanel;
		this.index = index;

		rCanvas = aPanel.readsCanvas;

		if (previous != null)
			previous.interrupt();

		start();
	}

	public void run()
	{
		rCanvas.overlays.add(this);

		// Darken the regions around the line/marker
		alphaEffect = 200;
		rCanvas.repaint();

		// Then wait for a while
		try { Thread.sleep(7000); }
		catch (InterruptedException e) { isOK = false; }

		// Before fading the other lines back to normality
		for (int i = 1; i <= 40 && isOK; i++)
		{
			// 40 * 5 = 200 (the starting alpha)
			alphaEffect = (int) (200 - (i * 5));
			rCanvas.repaint();

			// 25 * 40 = 1000 (1 second)
			try { Thread.sleep(25); }
			catch (InterruptedException e) { isOK = false; }
		}

		rCanvas.overlays.remove(this);
		rCanvas.repaint();
	}

	public void render(Graphics2D g)
	{
		g.setPaint(new Color(20, 20, 20, alphaEffect));

		int x1 = index * rCanvas.ntW;
		int x2 = x1 + rCanvas.ntW;

		g.fillRect(0, 0, x1, rCanvas.canvasH);
		g.fillRect(x2, 0, rCanvas.canvasW-x2, rCanvas.canvasH);
	}
}