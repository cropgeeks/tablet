// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.*;
import javax.swing.*;

import tablet.data.*;
import tablet.gui.*;

import scri.commons.gui.*;

/** Manages and paints the advanced tool tips for the reads canvas. */
class ReadsCanvasInfoPane implements IOverlayRenderer
{
	private Color bgColor;
	private Image lhArrow = Icons.getIcon("LHARROW").getImage();
	private Image rhArrow = Icons.getIcon("RHARROW").getImage();
	private Font titleFont, labelFont;
	private FontMetrics fmTitle;

	private OverviewCanvas oCanvas;
	private ScaleCanvas sCanvas;
	private ReadsCanvas rCanvas;

	// Variables that change as we move the mouse
	int lineIndex, mLineIndex;
	Read read, mRead;
	private ReadMetaData metaData, mMetaData;
	private Point mouse;
	private int x, y, w, h;
	private int vSpacing = 15;

	// Variables holding the data that gets drawn within the tooltip
	private String readName, posData, lengthData, cigar, mateContig, pairInfo;
	private String[] readInfo = new String[6];
	private String[] mateInfo = new String[6];

	ReadsCanvasInfoPane()
	{
		Color c = (Color) UIManager.get("info");
		bgColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), 190);

		titleFont = new Font("Dialog", Font.BOLD, 12);
		labelFont = new Font("Dialog", Font.PLAIN, 11);
	}

	void setAssemblyPanel(AssemblyPanel aPanel)
	{
		oCanvas = aPanel.overviewCanvas;
		sCanvas = aPanel.scaleCanvas;
		rCanvas = aPanel.readsCanvas;

		// Pre-build some font metrics for calculating string widths
		Image image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		fmTitle = g.getFontMetrics(titleFont);
		g.dispose();
	}

	void setMousePosition(Point mouse)
	{
		if (mouse == null)
			oCanvas.updateRead(-1, -1, -1);

		this.mouse = mouse;
	}

	/**
	 * Set the data for rendering the tooltip based on the Read and ReadMetaData
	 * provided. This sets up the string values for display.
	 */
	void setData(int lineIndex, Read read, ReadMetaData metaData, boolean isMate)
	{
		this.lineIndex = lineIndex;
		if(!isMate)
		{
			this.read = read;
			this.metaData = metaData;
		}
		else
		{
			mRead = read;
			this.mMetaData = metaData;
		}

		String insertSize, isProperPair, pairNumber;

		MatedRead mr = null;

		ReadNameData rnd = Assembly.getReadNameData(read);

		// Width and height of final overlay
		w = 300;
		h = 90;

		if (Assembly.hasCigar())
			h = 105;

		// Start and ending positions (against consensus)
		int readS = read.getStartPosition();
		int readE = read.getEndPosition();

		posData = RB.format("gui.viewer.ReadsCanvasInfoPane.from",
			(TabletUtils.nf.format(readS+1) + sCanvas.getUnpadded(readS)),
			(TabletUtils.nf.format(readE+1) + sCanvas.getUnpadded(readE)));

		if (Prefs.visHideUnpaddedValues)
			lengthData = RB.format("gui.viewer.ReadsCanvasInfoPane.length",
				TabletUtils.nf.format(read.length()));
		else
			lengthData = RB.format("gui.viewer.ReadsCanvasInfoPane.lengthUnpadded",
				TabletUtils.nf.format(read.length()),
				TabletUtils.nf.format(rnd.getUnpaddedLength()));

		// Name
		readName = rnd.getName();

		if (Assembly.hasCigar())
			cigar = RB.format("gui.viewer.ReadsCanvasInfoPane.cigar", rnd.getCigar());

		if(read instanceof MatedRead && metaData.getIsPaired() && metaData.getMateMapped())
		{
			mr = (MatedRead)read;
			insertSize =  RB.format("gui.viewer.ReadsCanvasInfoPane.insert", rnd.getInsertSize());
			isProperPair = rnd.isProperPair() ? RB.getString("gui.viewer.ReadsCanvasInfoPane.pairedProper") : RB.getString("gui.viewer.ReadsCanvasInfoPane.pairedProper");
			if(rnd.getNumberInPair() == 1)
				pairNumber = "(1/2) ";
			else if(rnd.getNumberInPair() == 2)
				pairNumber = "(2/2) ";
			else
				pairNumber = "";

			pairInfo = isProperPair + pairNumber + insertSize;

			mateContig = RB.format("gui.viewer.ReadsCanvasInfoPane.mateContig", rnd.getMateContig());
			if(rnd.getMateContig().equals(rCanvas.contig.getName()))
				mateContig = "";
		}
		else if(read instanceof MatedRead && metaData.getIsPaired() && !metaData.getMateMapped())
		{
			insertSize = isProperPair = pairNumber = mateContig = "";
			pairInfo = RB.getString("gui.viewer.ReadsCanvasInfoPane.mateUnmapped");
		}
		else
			insertSize = isProperPair = pairNumber = mateContig = pairInfo = "";

		adjustBoxSize();

		// Tell the overview canvas to paint this read too
		int offset = -rCanvas.offset;

		if(!isMate)
		{
			readInfo = new String[] {posData, lengthData, readName, cigar, pairInfo, mateContig};
			oCanvas.updateRead(lineIndex, readS+offset, readE+offset);
		}
		else
			mateInfo = new String[] {posData, lengthData, readName, cigar, pairInfo, mateContig};

		// Deal with setting up the multiple info panes.
		if(!isMate && mr != null && mr.getPair() != null)
		{
			ReadMetaData rmd = Assembly.getReadMetaData(mr.getPair(), false);
			if(rmd != null)
				setData(lineIndex, mr.getPair(), rmd, true);
			else
				mMetaData = null;
		}
		else if(!isMate)
			mMetaData = null;
		else if(!isMate && mr != null)
			mMetaData = null;
	}

	/**
	 * Alters the size of the tooltip boxes based on the sizes of the strings contained
	 * in the tooltip.
	 */
	private void adjustBoxSize()
	{
		if (!pairInfo.equals(""))
			h += vSpacing;
		if (!mateContig.equals(""))
			h += vSpacing;

		if (fmTitle.stringWidth(readName) > (w - 20))
			w = fmTitle.stringWidth(readName) + 20;
		if (fmTitle.stringWidth(posData) > (w - 20))
			w = fmTitle.stringWidth(posData) + 20;
		if (fmTitle.stringWidth(lengthData) > (w - 20))
			w = fmTitle.stringWidth(lengthData) + 20;
		if (Assembly.hasCigar() && fmTitle.stringWidth(cigar) > (w - 20))
			w = fmTitle.stringWidth(cigar) + 20;
		if (fmTitle.stringWidth(pairInfo) > (w - 20))
			w = fmTitle.stringWidth(pairInfo) + 20;
	}

	boolean isOverRead()
		{ return mouse != null; }

	public void render(Graphics2D g)
	{
		if (mouse == null || Prefs.visInfoPaneActive == false)
			return;

		MatedRead pr = null;
		if(read instanceof MatedRead)
		{
			pr = (MatedRead)read;
			if(pr.getPair() != null)
				calculatePosition(h*2);
			else
				calculatePosition(h+55);
		}
		else
			calculatePosition(h);

		drawBox(g, false, readInfo, metaData, read);

		ReadMetaData rmd = Assembly.getReadMetaData(read, false);
		if(read instanceof MatedRead && rmd.getIsPaired() && rmd.getMateMapped())
		{
			g.drawLine(w/2, h, w/2, h+10);
			g.translate(-x, -y+h+10);

			if(mMetaData != null && mRead != null)
			{
				drawBox(g, true, mateInfo, mMetaData, mRead);
			}
			else
			{
				ReadNameData rnd = Assembly.getReadNameData(read);
				drawMateUnavailableBox(g, pr.getMatePos(), pr.isMateContig(), rnd.getMateContig());
			}
		}

		// Put the graphics origin back to where it was in case other overlay
		// renderers run after this one
		g.translate(-x, -y);
	}

	/**
	 * Draws the outline, background and title of the box for the tooltip.
	 */
	private void drawBasicBox(Graphics2D g, int mateH, String tempName)
	{
		g.translate(x, y);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// The background rectangle
		g.setColor(bgColor);
		g.fillRoundRect(0, 0, w - 1, mateH - 1, 10, 10);
		g.setColor(Color.black);
		g.drawRoundRect(0, 0, w - 1, mateH - 1, 10, 10);
		// And the text
		g.setFont(titleFont);
		g.drawString(tempName, 10, vSpacing);
		g.setFont(labelFont);
	}

	/**
	 * Draws the rest of the box in the usual state where we have a pair of reads
	 * which are both contained in the current data window.
	 */
	private void drawBox(Graphics2D g, boolean isMate, String[] rInfo, ReadMetaData mData, Read renderRead)
	{
		String pData = rInfo[0];
		String lData = rInfo[1];
		String rName = rInfo[2];
		String cig = rInfo[3];
		String pInfo = rInfo[4];
		String mContig = rInfo[5];
		String tempName;

		if(isMate)
			tempName = rName + " " + RB.getString("gui.viewer.ReadsCanvasInfoPane.mateRead");
		else
			tempName = rName;

		int arrowHeight = calculateElementHeight(pInfo, mContig);

		drawBasicBox(g, h, tempName);

		g.drawString(pData, 10, vSpacing*2);
		g.drawString(lData, 10, vSpacing*3);
		if (Assembly.hasCigar())
		{
			g.setColor(Color.blue);
			g.drawString(cig, 10, vSpacing*4);
		}

		g.drawString(pInfo, 10, vSpacing*5);

		if(!mContig.equals("") && !isMate)
			g.drawString(mContig, 10, vSpacing*6);

		if (mData.isComplemented())
			g.drawImage(lhArrow, w - 10 - lhArrow.getWidth(null), arrowHeight, null);
		else
			g.drawImage(rhArrow, 10, arrowHeight, null);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		renderSequence(g, pInfo, mContig, mData, renderRead);
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
			g.drawString(RB.getString("gui.viewer.ReadsCanvasInfoPane.outwithContig"), 10, vSpacing*3);
			g.setColor(Color.black);
			g.drawString(RB.format("gui.viewer.ReadsCanvasInfoPane.locatedInContig", mateContig, matePos), 10, vSpacing *4);
		}
		else
		{
			g.drawString(RB.getString("gui.viewer.ReadsCanvasInfoPane.outwithBamWindow"), 10, vSpacing*3);
			g.setColor(Color.black);
			g.drawString(RB.format("gui.viewer.ReadsCanvasInfoPane.positionInContig", TabletUtils.nf.format(matePos)), 10, vSpacing*4);
		}
	}

	private int calculateElementHeight(String pInfo, String mContig)
	{
		int elementHeight = 55;

		if (Assembly.hasCigar())
			elementHeight += 15;
		if (!pInfo.equals(""))
			elementHeight += 15;
		if (!mContig.equals(""))
			elementHeight += 15;

		return elementHeight;
	}

	private void renderSequence(Graphics2D g, String pInfo, String mContig, ReadMetaData rmd, Read renderRead)
	{
		int lineStart = calculateElementHeight(pInfo, mContig) + vSpacing;

		float xScale = renderRead.length() / (float) (w - 10);

		// Determine color offset
		int color = rmd.getColorSchemeAdjustment(Prefs.visColorScheme);

		for (int x = 10; x < w-10; x++)
		{
			// Working out where each pixel maps to in the data...
			int dataX = (int) (x * xScale);

			// Then drawing that data
			byte b = (byte) (color + rmd.getStateAt(dataX));

			g.setColor(rCanvas.colors.getColor(b));
			g.drawLine(x, lineStart, x, lineStart+10);
		}

		g.setColor(Color.darkGray);
		g.drawRect(10, lineStart, w-21, 10);
	}

	private void calculatePosition(int height)
	{
		// Decide where to draw (roughly)
		x = mouse.x + 15;
		y = mouse.y + 20;

		// Then adjust if the box would be offscreen to the right or bottom
		if (x + w >= rCanvas.pX2Max)
			x = rCanvas.pX2Max - w - 1;
		if (y + height >= rCanvas.pY2)
			y = rCanvas.pY2 - height - 1;
	}

	void copyReadNameToClipboard()
	{
		StringSelection selection = new StringSelection(Assembly.getReadNameData(read).getName());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
			selection, null);
	}

	void copyDataToClipboard()
	{
		String lb = System.getProperty("line.separator");
		String seq = metaData.toString();

		StringBuilder text = new StringBuilder(seq.length() + 500);
		text.append(readName).append(lb).append(posData).append(lb).append(lengthData).append(lb);
		if(Assembly.hasCigar())
			text.append(cigar).append(lb);

		if (metaData.isComplemented())
			text.append(RB.getString("gui.viewer.ReadsCanvasInfoPane.directionReverse")).append(lb).append(lb);
		else
			text.append(RB.getString("gui.viewer.ReadsCanvasInfoPane.directionForward")).append(lb).append(lb);

		// Produce a FASTA formatted string
		text.append(TabletUtils.formatFASTA(Assembly.getReadNameData(read).getName(), seq));

		StringSelection selection = new StringSelection(text.toString());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
			selection, null);
	}
}