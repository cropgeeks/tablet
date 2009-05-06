package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;

import tablet.data.*;

public abstract class ColorScheme
{
	public static final int STANDARD = 10;

	protected Contig contig;

	ColorScheme(Contig contig)
	{
		this.contig = contig;
	}

	public abstract BufferedImage getImage(byte data);

	public abstract Color getColor(byte data);
}