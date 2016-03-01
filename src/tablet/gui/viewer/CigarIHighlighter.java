// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;

import tablet.data.*;
import tablet.data.auxiliary.*;
import tablet.data.auxiliary.CigarFeature.*;

public class CigarIHighlighter extends AlphaOverlay
{
	private Integer insertBase = null;
	private CigarFeature cigarFeature;
	private boolean visible = false;

	public CigarIHighlighter(AssemblyPanel aPanel)
	{
		super(aPanel);

		start();
	}

	public void render(Graphics2D g)
	{
		if(insertBase == null || cigarFeature == null)
			return;

		g.setPaint(new Color(0, 0, 0, alphaEffect));

		int yS = rCanvas.pY1 / rCanvas.ntH;
		int yE = rCanvas.pY2 / rCanvas.ntH;
		int pX1 = rCanvas.pX1;
		int ntH = rCanvas.ntH;
		int readH = rCanvas.readH;
		int pX2Max = rCanvas.pX2Max;

		for (int row = yS; row <= yE; row++)
		{
			Read read = rCanvas.reads.getReadAt(row, insertBase);

			if (read == null)
				g.fillRect(pX1, row*ntH, pX2Max-pX1+1, ntH);

			else
			{
				boolean requiresPaint = true;
				for(CigarEvent insert : cigarFeature.getEvents())
				{
					if(insert.getRead().equals(read))
					{
						int w1 = rCanvas.getFirstRenderedPixel(read.s()) - pX1;
						g.fillRect(pX1, row * ntH, w1, readH);

						int x2 = rCanvas.getFirstRenderedPixel(read.e()+1);
						int w2 = pX2Max -x2;
						g.fillRect(x2, row * ntH, w2, readH);

						if (rCanvas.ntW <= 1)
							g.fillRect(pX1, row * ntH + readH, pX2Max, ntH - readH);

						requiresPaint = false;
					}
				}

				if(requiresPaint)
					g.fillRect(pX1, row*ntH, pX2Max-pX1+1, ntH);
			}
		}
	}

	private void add()
	{
		if (previous != null)
			previous.interrupt();
		previous = this;

		if(!visible)
		{
			visible = true;
			alphaEffect = 200;

			rCanvas.overlays.addFirst(this);
			rCanvas.repaint();
		}
	}

	private void remove()
	{
		previous = null;
		if(visible)
		{
			visible = false;
			alphaEffect = 0;

			rCanvas.overlays.remove(this);
			rCanvas.repaint();
		}
	}

	public boolean isVisible()
		{ return visible; }

	public void highlightFeature(CigarFeature cigarFeature)
	{
		this.insertBase = cigarFeature.getVisualPS()+1;
		this.cigarFeature = cigarFeature;
		add();
	}

	public void removeHighlight()
	{
		insertBase = null;
		cigarFeature = null;
		remove();
	}
}