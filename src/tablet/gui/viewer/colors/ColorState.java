package av.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;

import av.data.*;

abstract class ColorState
{
	// AWT representation of this color
	protected Color color;

	// Buffered image used to draw this to the canvas
	protected BufferedImage image;

	// Width and height of the image
	protected int w, h;

	ColorState(Color c, int w, int h)
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