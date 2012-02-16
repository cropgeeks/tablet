// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.util.ArrayList;
import tablet.data.*;
import tablet.gui.Prefs;

/**
 * Class which overlays names on top of a stack set upon request by the user.
 */
public class NameOverlayer extends Thread implements IOverlayRenderer
{
	private ReadsCanvas rCanvas;
	private int overlayOpacity;
	protected boolean isOK = true;
	private static NameOverlayer previous;
	private boolean hidden;

	public NameOverlayer(ReadsCanvas rCanvas, boolean hidden)
	{
		this.rCanvas = rCanvas;
		this.hidden = hidden;

		//if we're already animating cancel that animation and replace it with
		//this new one
		if (previous != null)
		{
			previous.interrupt();
			rCanvas.overlays.remove(previous);
		}
		previous = this;
	}

	/**
	 * The method which renders the names for display. This will be called by the
	 * overlayRenderer loop found in the paintComponent method of ReadsCanvas.
	 *
	 * @param g A Graphics2D object to carry out rendering.
	 */
	public void render(Graphics2D g)
	{
		if(!Prefs.visPacked)
		{
			//set to white paint with overlayOpacity, draw a rectangle the size
			//of the screen, then set the paint back to black
			g.setPaint(new Color(255, 255, 255, overlayOpacity));
			g.fillRect(rCanvas.pX1, rCanvas.pY1, rCanvas.pX2, rCanvas.pY2);
			g.setPaint(new Color(0, 0, 0, overlayOpacity));

			int yS = rCanvas.pY1 / rCanvas.ntH;

			int yE = yS + rCanvas.ntOnScreenY;
			if(yE >= rCanvas.ntOnCanvasY) yE = rCanvas.ntOnCanvasY-1;

			Font font = new Font("Monospaced", Font.PLAIN, rCanvas.ntH-2);
			g.setFont(font);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			ArrayList<Read> reads = rCanvas.reads.getReadNames(yS, yE);

			int jiggle = rCanvas.pY1 % rCanvas.ntH;

			// This factor "jiggles" the starting location of the text;
			int textJiggle = (3*rCanvas.ntH/4)-jiggle;

			int y = rCanvas.pY1 +textJiggle;

			//loop over the reads, drawing them on screen.
			for(Read read : reads)
			{
				ReadNameData rnd = Assembly.getReadNameData(read);
				g.drawString(rnd.getName(), ((read.s()-rCanvas.offset)*rCanvas.ntW)+5, y);
				y += rCanvas.ntH;
			}
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}
	}

	@Override
	public void run()
	{
		rCanvas.overlays.addFirst(this);

		for (int i = 1; i <= 40 && isOK; i++)
		{
			// 40 * 5 = 200 (the desired ending alpha)
			if(!hidden)
				overlayOpacity = (0 + (i * 5));
			else
				overlayOpacity = (200 - (i * 5));
			rCanvas.repaint();

			// 25 * 40 = 1000 (1 second)
			try { Thread.sleep(25); }
			catch (InterruptedException e) { isOK = false; }
		}

		if(hidden)
		{
			rCanvas.overlays.remove(this);
			rCanvas.repaint();
		}
	}
}