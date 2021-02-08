// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

import tablet.data.*;
import tablet.gui.*;

import scri.commons.gui.*;

/**
 * Master InfoPaneRenderer class, handles positioning and drawing of both
 * ReadsCanvasInfoPane and ReadCanvasPairInfoPane.
 */
class ReadsCanvasInfoPaneRenderer implements IOverlayRenderer
{
	private ReadsCanvas rCanvas;

	private Color bgColor;
	protected Image lhArrow = Icons.getIcon("LHARROW").getImage();
	protected Image rhArrow = Icons.getIcon("RHARROW").getImage();
	private Font titleFont, labelFont;
	private FontMetrics fmTitle;

	ReadsCanvasInfoPane.BoxData box1;
	ReadsCanvasInfoPane.BoxData box2;

	// Where is the mouse
	private Point mouse;
	// Where are we going to start drawing
	private int x, y, w;

	// Distance between each line of text
	private int lineSpacing = 15;
	private int yPos;

	ReadsCanvasInfoPaneRenderer(ReadsCanvas rCanvas)
	{
		this.rCanvas = rCanvas;

		Color c = (Color) UIManager.get("info");
		bgColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), 190);

		titleFont = new Font("Dialog", Font.BOLD, 12);
		labelFont = new Font("Dialog", Font.PLAIN, 11);

		// Pre-build some font metrics for calculating string widths
		Image image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		fmTitle = g.getFontMetrics(titleFont);
		g.dispose();
	}

	void setMousePosition(Point mouse)
		{ this.mouse = mouse; }

	public void render(Graphics2D g)
	{
		if (mouse == null || Prefs.visInfoPaneActive == false)
			return;

		// Reset drawing variables
		int h1 = 0, h2 = 0;
		w = 300;
		yPos = 0;

		// Width and height of box1
		if (box1 != null)
		{
			h1 = calculateHeight(box1);
			calculateWidth(box1);
		}

		// Width and height of box2
		if (box2 != null)
		{
			h2 = calculateHeight(box2);
			calculateWidth(box2);
		}

		calculatePosition(h1 + h2 + 10);
		g.translate(x, y);


		if (box1 != null)
			renderBox(g, box1, h1, 1);

		if (box2 != null)
		{
			g.drawLine(w/2, h1, w/2, h1+10);

			yPos = h1 + 10;
			renderBox(g, box2, h2, 2);
		}
	}

	private void renderBox(Graphics2D g, ReadsCanvasInfoPane.BoxData box, int h, int boxNum)
	{
		drawBoxBackground(g, h);
		drawStrings(g, box, boxNum);

		if (box.read != null)
		{
			drawArrow(g, box);
			drawSequence(g, box);
		}
	}

	// Draws the outline and background
	private void drawBoxBackground(Graphics2D g, int h)
	{
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// The background rectangle
		g.setColor(bgColor);
		g.fillRoundRect(0, yPos, w - 1, h - 1, 10, 10);
		g.setColor(Color.black);
		g.drawRoundRect(0, yPos, w - 1, h - 1, 10, 10);

		yPos += 15;
	}

	private void drawStrings(Graphics2D g, ReadsCanvasInfoPane.BoxData box, int boxNum)
	{
		// Title
		g.setFont(titleFont);
		g.drawString(box.readName, 10, yPos);
		g.setFont(labelFont);

		// Position
		if (box.posData != null)
			g.drawString(box.posData, 10, (yPos += lineSpacing));

		// Length
		if (box.lengthData != null)
			g.drawString(box.lengthData, 10, (yPos += lineSpacing));

		g.setColor(Color.blue);

		// Cigar
		if (box.cigar != null)
			g.drawString(box.cigar, 10, (yPos += lineSpacing));

		// ReadGroup
		if (box.readGroupInfo != null)
			g.drawString(box.readGroupInfo, 10, (yPos += lineSpacing));

		if (box.insertedBases != null)
			g.drawString(box.insertedBases, 10, (yPos += lineSpacing));

		// Pair Info
		if (box.pairInfo != null)
			g.drawString(box.pairInfo, 10, (yPos += lineSpacing));

		g.setColor(Color.red);

		// Mate status
		if (box.mateStatus != null)
			g.drawString(box.mateStatus, 10, (yPos += lineSpacing));

		g.setColor(Color.black);

		// Mate position
		if (box.matePosition != null)
			g.drawString(box.matePosition, 10, (yPos += lineSpacing));
	}

	private void drawArrow(Graphics2D g, ReadsCanvasInfoPane.BoxData box)
	{
		yPos += 10;

		if (box.rmd.isComplemented())
			g.drawImage(lhArrow, w - 10 - lhArrow.getWidth(null), yPos, null);
		else
			g.drawImage(rhArrow, 10, yPos, null);
	}

	private void drawSequence(Graphics2D g, ReadsCanvasInfoPane.BoxData box)
	{
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_OFF);

		yPos += 15;

		float xScale = box.read.length() / (float) (w - 10);

		for (int x = 10; x < w-10; x++)
		{
			// Working out where each pixel maps to in the data...
			int dataX = (int) (x * xScale);

			g.setColor(rCanvas.colors.getOverviewColor(box.rmd, dataX));
			g.drawLine(x, yPos, x, yPos+10);
		}

		g.setColor(Color.darkGray);
		g.drawRect(10, yPos, w-21, 10);
	}

	private void calculatePosition(int h)
	{
		// Decide where to draw (roughly)
		x = mouse.x + 15;
		y = mouse.y + 20;

		int pX2Max = rCanvas.pX2Max;
		int pY2 = rCanvas.pY2;

		// Then adjust if the box would be offscreen to the right or bottom
		if (x + w >=pX2Max)
			x = pX2Max - w - 1;
		if (y + h >= pY2)
			y = pY2 - h - 1;
	}

	// The basic box width is set to 300. We then check if any of the strings
	// are longer, and adjust the box wider if they are
	private void calculateWidth(ReadsCanvasInfoPane.BoxData box)
	{
		if (box.readName != null && fmTitle.stringWidth(box.readName) > (w - 20))
			w = fmTitle.stringWidth(box.readName) + 20;
		if (box.posData != null && fmTitle.stringWidth(box.posData) > (w - 20))
			w = fmTitle.stringWidth(box.posData) + 20;
		if (box.lengthData != null && fmTitle.stringWidth(box.lengthData) > (w - 20))
			w = fmTitle.stringWidth(box.lengthData) + 20;
		if (box.cigar != null && fmTitle.stringWidth(box.cigar) > (w - 20))
			w = fmTitle.stringWidth(box.cigar) + 20;
		if (box.readGroupInfo != null && fmTitle.stringWidth(box.readGroupInfo) > (w - 20))
			w = fmTitle.stringWidth(box.readGroupInfo) + 20;
		if (box.pairInfo != null && fmTitle.stringWidth(box.pairInfo) > (w - 20))
			w = fmTitle.stringWidth(box.pairInfo) + 20;
		if (box.insertedBases != null && fmTitle.stringWidth(box.insertedBases) > (w - 20))
			w = fmTitle.stringWidth(box.insertedBases) + 20;
		if (box.mateStatus != null && fmTitle.stringWidth(box.mateStatus) > (w - 20))
			w = fmTitle.stringWidth(box.mateStatus) + 20;
		if (box.matePosition != null && fmTitle.stringWidth(box.matePosition) > (w - 20))
			w = fmTitle.stringWidth(box.matePosition) + 20;
	}

	private int calculateHeight(ReadsCanvasInfoPane.BoxData box)
	{
		int boxH = 15;

		boxH += box.readName != null ? lineSpacing : 0;
		boxH += box.posData != null ? lineSpacing : 0;
		boxH += box.lengthData != null ? lineSpacing : 0;
		boxH += box.cigar != null ? lineSpacing : 0;
		boxH += box.readGroupInfo != null ? lineSpacing : 0;
		boxH += box.pairInfo != null ? lineSpacing : 0;
		boxH += box.insertedBases != null ? lineSpacing : 0;
		boxH += box.mateStatus != null ? lineSpacing : 0;
		boxH += box.matePosition != null ? lineSpacing : 0;

		// Add extra space for arrow and sequence data
		boxH += box.lengthData != null ? 30 : 0;

		return boxH;
	}
}