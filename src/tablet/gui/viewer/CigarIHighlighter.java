package tablet.gui.viewer;

import java.awt.Color;
import java.awt.Graphics2D;
import tablet.data.Read;
import tablet.data.auxiliary.CigarFeature;
import tablet.data.auxiliary.CigarFeature.Insert;

public class CigarIHighlighter extends AlphaOverlay
{
	private Integer insertBase = null;
	private CigarFeature cigarFeature;
	private boolean visible = false;

	public CigarIHighlighter(AssemblyPanel aPanel, Integer insertBase, CigarFeature cigarFeature)
	{
		super(aPanel);
		this.insertBase = insertBase;
		this.cigarFeature = cigarFeature;

		start();
	}

	public CigarIHighlighter(AssemblyPanel aPanel)
	{
		super(aPanel);
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

		int xS = (pX1 - offset) / ntW;
		int xE = (rCanvas.pX2 - offset) / ntW;

		int count = 0;

		for (int row = yS; row <= yE; row++)
		{
			Read read = rCanvas.reads.getReadAt(row, insertBase);
			if (read == null || cigarFeature == null)
			{
				g.fillRect(pX1, row*ntH, pX2Max-pX1+1, ntH);
			}
			if (read != null && cigarFeature != null)
			{
				boolean requiresPaint = true;
				for(Insert insert : cigarFeature.getInserts())
				{
					if(insert.getRead().equals(read))
					{
						int x1 = pX1;
						int w1 = (read.getStartPosition() - offset) * ntW - x1;
						g.fillRect(x1, row * ntH, w1, ntH);

						int x2 = (read.getEndPosition()+1 - offset) * ntW;
						int w2 = (pX2Max * ntW) -x2;
						g.fillRect(x2, row * ntH, w2, ntH);

						g.setColor(Color.BLACK);
						int x3 = (insertBase - offset) * ntW;
						// Top horizontal line
						//g.drawLine(x3-ntW, row*ntH, x3+ntW-1, row*ntH);
						g.fillRect(x3-(ntW/2), row*ntH, ntW, 2);
						// Vertical line
						//g.drawLine(x3, row*ntH, x3, row*ntH + ntH);
						g.fillRect(x3-1, row*ntH, 2, ntH);
						// Bottom horizontal line
						//g.drawLine(x3-ntW, row*ntH+ntH-1, x3+ntW-1, row*ntH+ntH-1);
						g.fillRect(x3-(ntW/2), row*ntH+ntH-2, ntW, 2);
						g.setPaint(new Color(0, 0, 0, alphaEffect));

						requiresPaint = false;
					}
				}
				if(requiresPaint == true)
					g.fillRect(pX1, row*ntH, pX2Max-pX1+1, ntH);
			}
		}
	}

	public void setMouseBase(Integer newBase)
	{
		this.insertBase = newBase;
	}

	public void setCigarFeature(CigarFeature cigarFeature)
	{
		this.cigarFeature = cigarFeature;
	}

	public void add()
	{
		if (previous != null)
			previous.interrupt();
		previous = this;

		if(!visible)
		{
			visible = true;
			alphaEffect = 200;

			rCanvas.overlays.addFirst(this);
		}
	}

	public void remove()
	{
		previous = null;
		if(visible)
		{
			visible = false;

			alphaEffect = 0;

			rCanvas.overlays.remove(this);
		}
	}

	public boolean isVisible()
		{ return visible; }
}
