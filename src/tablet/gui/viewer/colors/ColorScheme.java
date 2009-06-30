package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;

import tablet.data.*;

public abstract class ColorScheme
{
	public static final int STANDARD = 10;
	public static final int TEXT = 20;

	protected Contig contig;

	ColorScheme(Contig contig)
	{
		this.contig = contig;
	}

	public abstract BufferedImage getImage(int data);

	public abstract BufferedImage getConsensusImage(int data);

	public abstract Color getColor(int data);
}