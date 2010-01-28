// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

import tablet.gui.*;

class TextState extends ColorState
{
	TextState(String text, int w, int h, boolean useAlpha, boolean isDeltaBase)
	{
		super(null, w, h);

		if (isDeltaBase)
			color = Color.red;
		else
			color = Color.lightGray;

		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		g.setColor(new Color(235, 235, 235));
		Rectangle2D.Float r = new Rectangle2D.Float(0, 0, w, h);
		g.fill(r);

		if (useAlpha)
		{
			// Overlay for bases that are different from the consensus
			if (isDeltaBase)
				g.setPaint(new Color(255, 255, 255, 130));
			// Overlay for bases that are identical to the consensus
			else
				g.setPaint(new Color(20, 20, 20, Prefs.visVariantAlpha));

			g.fillRect(0, 0, w, h);
		}

		Font font = g.getFont().deriveFont(Font.PLAIN, h-3);
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();

		Rectangle2D bounds = fm.getStringBounds(text, g);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (isDeltaBase)
			g.setColor(Color.red);
		else
			g.setColor(Color.black);
		g.drawString(text,
			(int)((float)w/2-bounds.getWidth()/2),
			h - fm.getMaxDescent());

		g.dispose();
	}
}