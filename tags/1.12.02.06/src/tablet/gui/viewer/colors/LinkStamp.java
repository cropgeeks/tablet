// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;

public class LinkStamp extends Stamp
{
	LinkStamp(Color c, int w, int h)
	{
		super(c, w, h);

		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = environment.getDefaultScreenDevice();
		GraphicsConfiguration config = device.getDefaultConfiguration();

		image = config.createCompatibleImage(w, h, Transparency.BITMASK);
		Graphics2D g = image.createGraphics();

		g.setBackground(new Color(0, 0, 0, 0));
		g.clearRect(0, 0, w, h);

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