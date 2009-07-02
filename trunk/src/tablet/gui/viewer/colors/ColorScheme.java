package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

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


	/** Returns a DNA colouring scheme from the cache of schemes. */
	public static ColorScheme getDNA(int type, Contig contig, int w, int h)
	{
		ColorScheme scheme = null;

		if (type == STANDARD)
			scheme = new StandardColorScheme(contig, w, h);

		else
			scheme = new TextColorScheme(contig, w, h);

		return scheme;
	}

	/** Returns a PROTEIN colouring scheme from the cache of schemes. */
	public static ColorScheme getProtein(int type, Contig contig, int w, int h)
	{
		ColorScheme scheme = null;

		if (type == STANDARD)
			scheme = new ProteinClassificationColorScheme(contig, w, h);

		else
			scheme = new ProteinTextColorScheme(contig, w, h);

		return scheme;
	}
}