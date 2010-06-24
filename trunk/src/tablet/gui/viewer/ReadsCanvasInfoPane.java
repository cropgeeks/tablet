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
	int lineIndex;
	Read read;
	ReadMetaData metaData;
	private Point mouse;
	private int x, y, w, h;

	// Variables holding the data that gets drawn within the tooltip
	private String readName, posData, lengthData, cigar;

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

	void setData(int lineIndex, Read read, ReadMetaData metaData)
	{
		this.lineIndex = lineIndex;
		this.read = read;
		this.metaData = metaData;

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
		readName = Assembly.getReadNameData(read).getName();

		if (Assembly.hasCigar())
			cigar = RB.format("gui.viewer.ReadsCanvasInfoPane.cigar", rnd.getCigar());

		// Determine longest string
		if (fmTitle.stringWidth(readName) > (w-20))
			w = fmTitle.stringWidth(readName) + 20;
		if (fmTitle.stringWidth(posData) > (w-20))
			w = fmTitle.stringWidth(posData) + 20;
		if (fmTitle.stringWidth(lengthData) > (w-20))
			w = fmTitle.stringWidth(lengthData) + 20;
		if (Assembly.hasCigar() && fmTitle.stringWidth(cigar) > (w-20))
			w = fmTitle.stringWidth(cigar) + 20;

		// Tell the overview canvas to paint this read too
		int offset = -rCanvas.offset;
		oCanvas.updateRead(lineIndex, readS+offset, readE+offset);
	}

	boolean isOverRead()
		{ return mouse != null; }

	public void render(Graphics2D g)
	{
		if (mouse == null || Prefs.visInfoPaneActive == false)
			return;

		int arrowHeight = 55;

		if (Assembly.hasCigar())
			arrowHeight = 70;

		calculatePosition();
		g.translate(x, y);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// The background rectangle
		g.setColor(bgColor);
		g.fillRoundRect(0, 0, w-1, h-1, 10, 10);
		g.setColor(Color.black);
		g.drawRoundRect(0, 0, w-1, h-1, 10, 10);

		// And the text
		g.setFont(titleFont);
		g.drawString(readName, 10, 15);
		g.setFont(labelFont);
		g.drawString(posData, 10, 30);
		g.drawString(lengthData, 10, 45);
		if (Assembly.hasCigar())
		{
			g.setColor(Color.blue);
			g.drawString(cigar, 10, 60);
		}

		// Complemented/uncomplemented arrow
		if (metaData.isComplemented())
			g.drawImage(lhArrow, w-10-lhArrow.getWidth(null), arrowHeight, null);
		else
			g.drawImage(rhArrow, 10, arrowHeight, null);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		renderSequence(g);

		// Put the graphics origin back to where it was in case other overlay
		// renderers run after this one
		g.translate(-x, -y);
	}

	private void renderSequence(Graphics2D g)
	{
		int lineStart = 70;

		if (Assembly.hasCigar())
			lineStart = 85;

		ReadMetaData rmd = Assembly.getReadMetaData(read, false);

		float xScale = read.length() / (float) (w - 10);

		byte isComplemented = (byte) (rmd.isComplemented() ? 20 : 0);

		for (int x = 10; x < w-10; x++)
		{
			// Working out where each pixel maps to in the data...
			int dataX = (int) (x * xScale);

			// Then drawing that data
			byte b = (byte) (isComplemented + rmd.getStateAt(dataX));

			g.setColor(rCanvas.colors.getColor(b));
			g.drawLine(x, lineStart, x, lineStart+10);
		}

		g.setColor(Color.darkGray);
		g.drawRect(10, lineStart, w-21, 10);
	}

	private void calculatePosition()
	{
		// Decide where to draw (roughly)
		x = mouse.x + 15;
		y = mouse.y + 20;

		// Then adjust if the box would be offscreen to the right or bottom
		if (x + w >= rCanvas.pX2Max)
			x = rCanvas.pX2Max - w - 1;
		if (y + h >= rCanvas.pY2)
			y = rCanvas.pY2 - h - 1;
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
		text.append(readName + lb + posData + lb + lengthData + lb);
		if(Assembly.hasCigar())
			text.append(cigar + lb);

		if (metaData.isComplemented())
			text.append("Read direction is REVERSE" + lb + lb);
		else
			text.append("Read direction is FORWARD" + lb + lb);

		// Produce a FASTA formatted string
		text.append(TabletUtils.formatFASTA(Assembly.getReadNameData(read).getName(), seq));

		StringSelection selection = new StringSelection(text.toString());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
			selection, null);
	}
}