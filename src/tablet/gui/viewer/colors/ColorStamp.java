// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

import tablet.gui.*;

class ColorStamp extends Stamp
{
	ColorStamp(String text, Color c, int w, int h, boolean useAlpha, boolean isDeltaBase)
	{
		super(c, w, h);

		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		Color c1 = c.brighter();
		Color c2 = c.darker();
		// Only use gradient paints for bitmaps of 2 pixels or wider
		if (w > 1)
			g.setPaint(new GradientPaint(0, 0, c1, w, h, c2));
		else
			g.setColor(c);

		Rectangle2D.Float r = new Rectangle2D.Float(0, 0, w, h);
		g.fill(r);

		if (useAlpha)
		{
			// Overlay for bases that are different from the consensus
			if (isDeltaBase && displayInRed(text))
			{
				g.setPaint(new Color(255, 255, 255, 130));
				color = c1;
				if (Prefs.visTagVariants)
					ovColor = Color.red;
			}
			// Overlay for bases that are identical to the consensus
			else
				g.setPaint(new Color(20, 20, 20, Prefs.visVariantAlpha));

			g.fillRect(0, 0, w, h);

			// Extract the colour back from the bitmap (so we can use variant
			// highlighting when painting with Colors rather than bitmaps
			int clr = image.getRGB(0,0);
			int rd = (clr & 0x00ff0000) >> 16;
			int gr = (clr & 0x0000ff00) >> 8;
			int bl =  clr & 0x000000ff;
			color = new Color(rd, gr, bl);
		}

		if (Prefs.visEnableText)
		{
			Font font = g.getFont().deriveFont(Font.PLAIN, h-3);
			g.setFont(font);
			FontMetrics fm = g.getFontMetrics();

			Rectangle2D bounds = fm.getStringBounds(text, g);

			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			if (isDeltaBase && displayInRed(text))
				g.setColor(ColorPrefs.get("User.Nucleotides.DeltaText"));
			else
				g.setColor(ColorPrefs.get("User.Nucleotides.Text"));

			g.drawString(text,
				(int)((float)w/2-bounds.getWidth()/2),
				h - fm.getMaxDescent());
		}

		g.dispose();
	}
}