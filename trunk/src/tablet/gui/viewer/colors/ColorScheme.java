package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import tablet.data.*;

public abstract class ColorScheme
{
	public static final int STANDARD = 10;
	public static final int TEXT = 20;

	// Maintains a collection of every colour scheme created so far
	private static Hashtable<String, ColorScheme> schemes =
		new Hashtable<String, ColorScheme>();

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
		{
			scheme = schemes.get("STDDNA" + w);
			if (scheme == null)
			{
				scheme = new StandardColorScheme(contig, w, h);
				schemes.put("STDDNA" + w, scheme);
			}
		}

		else
		{
			scheme = schemes.get("TXTDNA" + w);
			if (scheme == null)
			{
				scheme = new TextColorScheme(contig, w, h);
				schemes.put("TXTDNA" + w, scheme);
			}
		}

		return scheme;
	}

	/** Returns a PROTEIN colouring scheme from the cache of schemes. */
	public static ColorScheme getProtein(int type, Contig contig, int w, int h)
	{
		ColorScheme scheme = null;

		if (type == STANDARD)
		{
			scheme = schemes.get("STDPRO" + w);
			if (scheme == null)
			{
				scheme = new ProteinClassificationColorScheme(contig, w, h);
				schemes.put("STDPRO" + w, scheme);
			}
		}

		else
		{
			scheme = schemes.get("TXTPRO" + w);
			if (scheme == null)
			{
				scheme = new ProteinTextColorScheme(contig, w, h);
				schemes.put("TXTPRO" + w, scheme);
			}
		}

		return scheme;
	}
}