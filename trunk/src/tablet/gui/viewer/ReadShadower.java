package tablet.gui.viewer;

import java.awt.Color;
import java.awt.Graphics2D;

import tablet.data.Read;
import tablet.data.auxiliary.VisualContig;
import tablet.gui.Prefs;

class ReadShadower implements IOverlayRenderer
{
	private int overlayOpacity = 75;
	private Integer mouseBase;
	private AssemblyPanel aPanel;
	private ReadsCanvas rCanvas;

	public ReadShadower(AssemblyPanel aPanel, boolean hidden)
	{
		this.aPanel = aPanel;
		this.rCanvas = aPanel.readsCanvas;
	}

	/**
	 * Draws the read shadowing on top of the reads canvas.
	 */
	public void render(Graphics2D g)
	{
		g.setPaint(new Color(0, 0, 0, overlayOpacity));
		if(Prefs.visCentreReadShadower)
			renderLockedToMiddle(g);
		else
			renderFreeFlowing(g);
	}

	/**
	 * Renders the read shadowing with respect to the centre of the reads canvas.
	 * This is represented by a line down the middle of the reads canvas.
	 */
	private void renderLockedToMiddle(Graphics2D g)
	{
		int mid = (rCanvas.pX1 + ((rCanvas.pX2Max-rCanvas.pX1)/2));
		int top = rCanvas.pY1 / rCanvas.ntH;
		int bottom = rCanvas.pY2 / rCanvas.ntH;

		for (int row = top; row <= bottom; row++)
		{
			Read read = rCanvas.reads.getReadAt(row, mid/rCanvas.ntW+rCanvas.offset);
			if (read != null)
			{
				g.fillRect((read.getStartPosition()-rCanvas.offset)*rCanvas.ntW, row*rCanvas.ntH, (read.getEndPosition()-read.getStartPosition()+1)*rCanvas.ntW, rCanvas.ntH);
			}
		}
		// Draws a vertical line down the middle of the display
		g.setColor(Color.black);
		g.drawLine(mid, rCanvas.pY1, mid, rCanvas.pY2);
	}

	/**
	 * Draws the read shadowing relative to the base the mouse is currently over,
	 * or a base that the shadowing has been locked to.
	 */
	private void renderFreeFlowing(Graphics2D g)
	{
		Integer intersectPosition = determineIntersectPosition();

		if(intersectPosition != null)
		{
			int yS = rCanvas.pY1 / rCanvas.ntH;
			int yE = rCanvas.pY2 / rCanvas.ntH;
			for (int row = yS; row <= yE; row++)
			{
				Read read = rCanvas.reads.getReadAt(row, intersectPosition);
				if (read != null)
				{
					g.fillRect((read.getStartPosition() - rCanvas.offset) * rCanvas.ntW, row * rCanvas.ntH, ((read.getEndPosition() - rCanvas.offset) - (read.getStartPosition() - rCanvas.offset) + 1) * rCanvas.ntW, rCanvas.ntH);
				}
			}
			// Draws a vertical line down the display
			g.setColor(Color.black);
			g.drawLine((intersectPosition - rCanvas.offset) * rCanvas.ntW + rCanvas.ntW / 2, rCanvas.pY1, (intersectPosition - rCanvas.offset) * rCanvas.ntW + rCanvas.ntW / 2, rCanvas.pY2);
		}
	}

	/**
	 * Works out if we are drawing relative to the current mouse position, or a
	 * position the shadowing has been locked to.
	 */
	private Integer determineIntersectPosition()
	{
		Integer intersectPosition;
		VisualContig vContig = aPanel.getVisualContig();

		// The shadowing has been locked to a base
		if(vContig.getLockedBase() != null)
		{
			intersectPosition = vContig.getLockedBase();
			Prefs.visReadShadowerLocked = true;
			rCanvas.readsCanvasML.getRCanvasMenu().getMIntersectLock().setSelected(Prefs.visReadShadowerLocked);
		}

		// The shadowing was locked to a base, but on another contig...set to free flowing
		else
		{
			Prefs.visReadShadowerLocked = false;
			rCanvas.readsCanvasML.getRCanvasMenu().getMIntersectLock().setSelected(Prefs.visReadShadowerLocked);
			intersectPosition = mouseBase;
		}

		return intersectPosition;
	}

	public void setMouseBase(Integer mouseBase)
		{	this.mouseBase = mouseBase;	}

	public void setLocked(boolean locked)
	{
		Prefs.visReadShadowerLocked = locked;

		if(Prefs.visReadShadowerLocked)
		{
			VisualContig vContig = aPanel.getVisualContig();
			vContig.setLockedBase(mouseBase);
		}
	}
}
