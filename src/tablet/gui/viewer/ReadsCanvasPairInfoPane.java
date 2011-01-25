// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;

import scri.commons.gui.RB;

import tablet.data.*;
import tablet.gui.*;

class ReadsCanvasPairInfoPane extends ReadsCanvasInfoPane
{
	private boolean mateAvailable = true;
	
	void draw(Graphics2D g)
	{	
		if(mateAvailable)
			drawBox(g);
		else
		{
			MatedRead pr = (MatedRead)mRead;
			ReadNameData rnd = Assembly.getReadNameData(mRead);
			drawMateUnavailableBox(g, pr.getMatePos(), pr.isMateContig(), rnd.getMateContig());
		}
	}

	/**
	 * Draws the rest of the box in the usual state where we have a pair of reads
	 * which are both contained in the current data window.
	 */
	protected void drawBox(Graphics2D g)
	{
		int elementNo = 1;
		String tempName = readName + " " + RB.getString("gui.viewer.ReadsCanvasInfoPane.mateRead");

		drawBasicBox(g, h, tempName);

		drawSharedStrings(g, elementNo);

		if (metaData.isComplemented())
			g.drawImage(lhArrow, w - 10 - lhArrow.getWidth(null), elementHeight, null);
		else
			g.drawImage(rhArrow, 10, elementHeight, null);
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		renderSequence(g);
	}

	/**
	 * Draws the rest of the tooltip when the mate is in another contig, or outwith
	 * the current BAM data window.
	 */
	private void drawMateUnavailableBox(Graphics2D g, int matePos, boolean isMateContig, String mateContig)
	{
		String tempName = readName + " " + RB.getString("gui.viewer.ReadsCanvasInfoPane.mateRead");
		int mateH = 70;

		drawBasicBox(g, mateH, tempName);

		g.setColor(Color.red);
		if(!isMateContig)
		{
			g.drawString(RB.getString("gui.viewer.ReadsCanvasInfoPane.outwithContig"), 10, lineSpacing*3);
			g.setColor(Color.black);
			g.drawString(RB.format("gui.viewer.ReadsCanvasInfoPane.locatedInContig", mateContig, matePos), 10, lineSpacing *4);
		}
		else
		{
			g.drawString(RB.getString("gui.viewer.ReadsCanvasInfoPane.outwithBamWindow"), 10, lineSpacing*3);
			g.setColor(Color.black);
			g.drawString(RB.format("gui.viewer.ReadsCanvasInfoPane.positionInContig", TabletUtils.nf.format(matePos)), 10, lineSpacing*4);
		}
	}

	public boolean isMateAvailable()
	{
		return mateAvailable;
	}

	public void setMateAvailable(boolean mateAvailable)
	{
		this.mateAvailable = mateAvailable;
	}
}