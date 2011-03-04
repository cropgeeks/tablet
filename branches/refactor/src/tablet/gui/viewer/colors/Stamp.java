// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;

abstract class Stamp
{
	// AWT representation of this color
	protected Color color;

	// Buffered image used to draw this to the canvas
	protected BufferedImage image;

	// Width and height of the image
	protected int w, h;

	Stamp(Color c, int w, int h)
	{
		this.color = c;
		this.w = w;
		this.h = h;
	}

	public BufferedImage getImage()
		{ return image; }

	public Color getColor()
		{ return color; }
}