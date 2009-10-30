// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

/**
 * Abstract base class of any highlighters that modify the alpha overlay value
 * of the main canvas. Because all such highlighters inherit from this class we
 * can ensure that only one can ever be running at a single point in time.
 */
abstract class AlphaOverlay extends Thread implements IOverlayRenderer
{
	protected static AlphaOverlay previous;

	protected AssemblyPanel aPanel;
	protected ReadsCanvas rCanvas;

	protected boolean isOK = true;
	protected int alphaEffect = 0;

	AlphaOverlay(AssemblyPanel aPanel)
	{
		this.aPanel = aPanel;
		rCanvas = aPanel.readsCanvas;

		if (previous != null)
			previous.interrupt();
		previous = this;
	}

	public void run()
	{
		rCanvas.overlays.addFirst(this);

		// Darken the regions around the line/marker
		alphaEffect = 200;
		rCanvas.repaint();

		// Then wait for a while
		try { Thread.sleep(5000); }
		catch (InterruptedException e) { isOK = false; }

		// Before fading the other lines back to normality
		for (int i = 1; i <= 40 && isOK; i++)
		{
			// 40 * 5 = 200 (the starting alpha)
			alphaEffect = (200 - (i * 5));
			rCanvas.repaint();

			// 25 * 40 = 1000 (1 second)
			try { Thread.sleep(25); }
			catch (InterruptedException e) { isOK = false; }
		}

		rCanvas.overlays.remove(this);
		rCanvas.repaint();
	}
}