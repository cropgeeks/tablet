// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
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
		if(insertBase == null)
			return;

		g.setPaint(new Color(0, 0, 0, alphaEffect));

		int yS = rCanvas.pY1 / rCanvas.ntH;
		int yE = rCanvas.pY2 / rCanvas.ntH;
		int pX1 = rCanvas.pX1;
		int ntH = rCanvas.ntH;
		int pX2Max = rCanvas.pX2Max;
		int offset = rCanvas.offset;
		int ntW = rCanvas.ntW;

		if (cigarFeature == null)
			return;

		for (int row = yS; row <= yE; row++)
		{
			Read read = rCanvas.reads.getReadAt(row, insertBase);

			if (read == null)
				g.fillRect(pX1, row*ntH, pX2Max-pX1+1, ntH);

			if (read != null)
			{
				boolean requiresPaint = true;
				for(Insert insert : cigarFeature.getInserts())
				{
					if(insert.getRead().equals(read))
					{
						int x1 = pX1;
						int w1 = (read.s() - offset) * ntW - x1;
						g.fillRect(x1, row * ntH, w1, ntH);

						int x2 = (read.e()+1 - offset) * ntW;
						int w2 = (pX2Max * ntW) -x2;
						g.fillRect(x2, row * ntH, w2, ntH);

						requiresPaint = false;
					}
				}

				if(requiresPaint == true)
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