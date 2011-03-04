// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.*;
import java.util.ArrayList;
import javax.swing.*;

import tablet.data.*;
import tablet.gui.*;

import scri.commons.gui.*;
import tablet.data.auxiliary.*;
import tablet.data.auxiliary.CigarFeature.Insert;

/** Manages and paints the advanced tool tips for the reads canvas. */
class ReadsCanvasInfoPane
{
	private Color bgColor;
	protected Image lhArrow = Icons.getIcon("LHARROW").getImage();
	protected Image rhArrow = Icons.getIcon("RHARROW").getImage();
	private Font titleFont, labelFont;
	private FontMetrics fmTitle;

	private OverviewCanvas oCanvas;
	private ScaleCanvas sCanvas;
	private AssemblyPanel aPanel;
	ReadsCanvas rCanvas;

	// Variables that change as we move the mouse
	protected int lineIndex, x, y, w, h;
	protected Read read, mRead;
	protected ReadMetaData metaData;
	protected Point mouse;
	protected int lineSpacing = 15;
	protected int basicHeight = 55;
	protected int elementHeight;

	// Variables holding the data that gets drawn within the tooltip
	protected String readName, posData, lengthData, cigar, mateContig, pairInfo, insertedBases;

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
		this.aPanel = aPanel;

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
	void setData(int lineIndex, Read read, ReadMetaData metaData)
	{
		if (read == null)
			return;

		this.lineIndex = lineIndex;
		this.read = read;
		this.metaData = metaData;

		ReadNameData rnd = Assembly.getReadNameData(read);

		// Width and height of final overlay
		w = 300;
		h = 90;

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

		setupPairInfo(rnd);
		setupInsertedBaseInfo(read);

		adjustBoxSize();
	}

	void updateOverviewCanvas()
	{
		// Tell the overview canvas to paint this read too
		int offset = -rCanvas.offset;

		oCanvas.updateRead(lineIndex, read.getStartPosition()+offset, read.getEndPosition()+offset);
	}

	private void setupInsertedBaseInfo(Read read)
	{
		insertedBases = "";
		// TODO
		if(mouse == null || aPanel.getVisualContig().getTrackCount() <= 0)
			return;

		ArrayList<Feature> features = aPanel.getVisualContig().getTrack(0).getFeatures(read.getStartPosition(), read.getEndPosition());
		for (Feature feature : features)
		{
			if (!(feature.getGFFType().equals("CIGAR-I")))
				continue;

			CigarFeature cigarFeature = (CigarFeature) feature;
			for (Insert insert : cigarFeature.getInserts())
			{
				if (insert.getRead().equals(read))
				{
					if (insertedBases.equals(""))
						insertedBases += RB.format("gui.viewer.ReadsCanvasInfoPane.inserted", insert.getInsertedBases());
					else
						insertedBases += " - " + insert.getInsertedBases();
				}
			}
		}
	}

	private void setupPairInfo(ReadNameData rnd)
	{
		String insertSize, isProperPair, pairNumber;

		if(read instanceof MatedRead && metaData.getIsPaired() && metaData.getMateMapped())
		{
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
	}

	/**
	 * Alters the size of the tooltip boxes based on the sizes of the strings contained
	 * in the tooltip.
	 */
	private void adjustBoxSize()
	{
		elementHeight = basicHeight;
		if (Assembly.hasCigar())
		{
			h += lineSpacing;
			elementHeight += lineSpacing;
		}
		if (!pairInfo.equals(""))
		{
			h += lineSpacing;
			elementHeight += lineSpacing;
		}
		if (!mateContig.equals(""))
		{
			h += lineSpacing;
			elementHeight += lineSpacing;
		}
		if (!insertedBases.equals(""))
		{
			h += lineSpacing;
			elementHeight += lineSpacing;
		}

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
		if (fmTitle.stringWidth(insertedBases) > (w - 20))
			w = fmTitle.stringWidth(insertedBases) + 20;
	}

	boolean isOverRead()
		{ return mouse != null; }


	/**
	 * Draws the outline, background and title of the box for the tooltip.
	 */
	protected void drawBasicBox(Graphics2D g, int mateH, String tempName)
	{
		//g.translate(x, y);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// The background rectangle
		g.setColor(bgColor);
		g.fillRoundRect(0, 0, w - 1, mateH - 1, 10, 10);
		g.setColor(Color.black);
		g.drawRoundRect(0, 0, w - 1, mateH - 1, 10, 10);
		// And the text
		g.setFont(titleFont);
		g.drawString(tempName, 10, lineSpacing);
		g.setFont(labelFont);
	}

	protected int drawSharedStrings(Graphics2D g, int lineNo)
	{
		g.drawString(posData, 10, lineSpacing * ++lineNo);
		g.drawString(lengthData, 10, lineSpacing * ++lineNo);

		if (Assembly.hasCigar())
		{
			g.setColor(Color.blue);
			g.drawString(cigar, 10, lineSpacing * ++lineNo);
			if (!insertedBases.equals(""))
				g.drawString(insertedBases, 10, lineSpacing * ++lineNo);
		}

		if (pairInfo.equals(RB.getString("gui.viewer.ReadsCanvasInfoPane.mateUnmapped")))
			g.setColor(Color.red);

		g.drawString(pairInfo, 10, lineSpacing * ++lineNo);

		return lineNo;
	}

	/**
	 * Draws the rest of the box in the usual state where we have a pair of reads
	 * which are both contained in the current data window.
	 */
	protected void drawBox(Graphics2D g)
	{
		if(read == null)
			return;

		int lineNo = 1;

		drawBasicBox(g, h, readName);

		lineNo = drawSharedStrings(g, lineNo);

		if(!mateContig.equals(""))
			g.drawString(mateContig, 10, lineSpacing * ++lineNo);

		if (metaData.isComplemented())
			g.drawImage(lhArrow, w - 10 - lhArrow.getWidth(null), elementHeight, null);
		else
			g.drawImage(rhArrow, 10, elementHeight, null);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		renderSequence(g);
	}

	protected void renderSequence(Graphics2D g)
	{
		int lineStart = elementHeight + lineSpacing;

		float xScale = read.length() / (float) (w - 10);

		for (int x = 10; x < w-10; x++)
		{
			// Working out where each pixel maps to in the data...
			int dataX = (int) (x * xScale);

			g.setColor(rCanvas.colors.getColor(metaData, dataX));
			g.drawLine(x, lineStart, x, lineStart+10);
		}

		g.setColor(Color.darkGray);
		g.drawRect(10, lineStart, w-21, 10);
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

	public void setmRead(Read mRead)
	{
		this.mRead = mRead;
	}
}