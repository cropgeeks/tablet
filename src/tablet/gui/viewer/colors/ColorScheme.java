package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import tablet.data.*;

public abstract class ColorScheme
{
	public static final int STANDARD = 10;
	public static final int TEXT = 20;

	ColorScheme()
	{
	}

	public abstract BufferedImage getImage(int data);

	public abstract BufferedImage getConsensusImage(int data);

	public abstract Color getColor(int data);


	/** Returns a DNA colouring scheme from the cache of schemes. */
	public static ColorScheme getDNA(int type, int w, int h)
	{
		ColorScheme scheme = null;

		if (type == STANDARD)
			scheme = new StandardColorScheme(w, h);

		else
			scheme = new TextColorScheme(w, h);

		return scheme;
	}

	/** Returns a PROTEIN colouring scheme from the cache of schemes. */
	public static ColorScheme getProtein(int type, int w, int h)
	{
		ColorScheme scheme = null;

		if (type == STANDARD)
			scheme = new ProteinClassificationColorScheme(w, h);

		else
			scheme = new ProteinTextColorScheme(w, h);

		return scheme;
	}
}