package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

class StandardColorState extends ColorState
{
	StandardColorState(String text, Color c, int w, int h, boolean highlight)
	{
		super(c, w, h);

		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		Color c1 = c.brighter();
		Color c2 = c.darker();
		g.setPaint(new GradientPaint(0, 0, c1, w, h, c2));

		Rectangle2D.Float r = new Rectangle2D.Float(0, 0, w, h);
		g.fill(r);

		if (highlight)
		{
			g.setPaint(new Color(255, 255, 255, 130));
			g.fillRect(0, 0, w, h);
		}

		Font font = g.getFont().deriveFont(Font.PLAIN, h-3);
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();

		Rectangle2D bounds = fm.getStringBounds(text, g);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (highlight)
			g.setColor(Color.red);
		else
			g.setColor(Color.black);
		g.drawString(text,
			(int)((float)w/2-bounds.getWidth()/2),
			h - fm.getMaxDescent());

		g.dispose();
	}
}