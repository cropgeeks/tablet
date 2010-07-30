package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;

public class PairLinkColorState extends ColorState
{
	PairLinkColorState(Color c, int w, int h)
	{
		super(c, w, h);

		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		g.setPaint(Color.white);
		g.fillRect(0, 0, w, h);

		g.setPaint(c);

		for(int i=0; i < w; i+=2)
		{
			if(i % 6 == 0)
				g.drawLine(i-1, h/2, i+1, h/2);
			else
				g.drawLine(i, h/2, i, h/2);
		}

		g.dispose();
	}
}
