// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

import tablet.gui.*;

class ProteinClassificationColorState extends ColorState
{
	ProteinClassificationColorState(String txt, Color c, int w, int h, int chop)
	{
		super(c, w, h);

		int w3 = w*3;

		// Make a main buffer that is three times the width of what's needed
		BufferedImage tmpImg = new BufferedImage(
			w3, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = tmpImg.createGraphics();

		// Paint the as it would look if stretched over 3 bases
		Color c1 = c.brighter();
		Color c2 = c.darker();
		g.setPaint(new GradientPaint(0, 0, c1, w3, h, c2));

		Rectangle2D.Float r = new Rectangle2D.Float(0, 0, w3, h);
		g.fill(r);

		Font font = g.getFont().deriveFont(Font.PLAIN, h-3);
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();

		Rectangle2D bounds = fm.getStringBounds(txt, g);

		if (Prefs.visEnableText)
		{
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(Color.black);
			g.drawString(txt,
				(int)((float)w3/2-bounds.getWidth()/2),
				h - fm.getMaxDescent());
		}

		g.dispose();


		// Now chop the image into three and save only the third required
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		g = image.createGraphics();


		// Centre image
		if (chop == 0)
			g.drawImage(tmpImg, 0, 0, w, h, w, 0, w*2, h, null);

		// Left image
		else if (chop == 1)
			g.drawImage(tmpImg, 0, 0, w, h, 0, 0, w, h, null);
		// Right image
		else
			g.drawImage(tmpImg, 0, 0, w, h, w*2, 0, w*3, h, null);


		g.dispose();
	}
}