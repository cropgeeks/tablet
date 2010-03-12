// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.gui.*;

public abstract class ColorScheme
{
	public static final int STANDARD = 10;
	public static final int CLASSIC = 20;
	public static final int DIRECTION = 30;

	ColorScheme()
	{
	}

	public abstract BufferedImage getImage(int data);

	public abstract BufferedImage getConsensusImage(int data);

	public abstract Color getColor(int data);


	/** Returns a DNA colouring scheme from the cache of schemes. */
	public static ColorScheme getDNA(int type, int w, int h)
	{
		// Ensure the correct type of pad character rendering is used
		if (Prefs.visPadCharType == 0)
			Sequence.PAD = "*";
		else if (Prefs.visPadCharType == 1)
			Sequence.PAD = "-";

		ColorScheme scheme = null;

		if (type == STANDARD)
			scheme = new StandardColorScheme(w, h, false);

		else if (type == CLASSIC)
			scheme = new TextColorScheme(w, h);

		else if (type == DIRECTION)
			scheme = new DirectionColorScheme(w, h);

		return scheme;
	}

	/** Returns a PROTEIN colouring scheme from the cache of schemes. */
	public static ColorScheme getProtein(int type, int w, int h)
	{
		// Ensure the correct type of pad character rendering is used
		if (Prefs.visStopCharType == 0)
			ProteinTranslator.setStopCharacter(".");
		else if (Prefs.visStopCharType == 1)
			ProteinTranslator.setStopCharacter("*");

		ColorScheme scheme = null;

		if (type == STANDARD)
			scheme = new ProteinClassificationColorScheme(w, h);

		else
			scheme = new ProteinTextColorScheme(w, h);

		return scheme;
	}
}