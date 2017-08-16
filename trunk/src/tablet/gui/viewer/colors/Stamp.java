// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;

import tablet.data.*;
import tablet.gui.*;

abstract class Stamp
{
	// AWT representation of this color
	protected Color color;
	// AWT representation of the overview color used (which is different for
	// deltas/variant bases
	protected Color ovColor;

	// Buffered image used to draw this to the canvas
	protected BufferedImage image;

	// Width and height of the image
	protected int w, h;

	Stamp(Color c, int w, int h)
	{
		this.color = c;
		this.ovColor = c;
		this.w = w;
		this.h = h;
	}

	public BufferedImage getImage()
		{ return image; }

	public Color getColor()
		{ return color; }

	public Color getOverviewColor()
		{ return ovColor; }


	// Special case for ?, *, or N characters (when deltas) that decides whether
	// they should be in red or not based on the user's settings
	protected boolean displayInRed(String text)
	{
		if (text.equals("?") || text.equals(Sequence.PAD) || text.equals("N"))
			return (Prefs.visNeverTagUnknownBases == false);

		// All other delta bases should always be in red
		return true;
	}
}