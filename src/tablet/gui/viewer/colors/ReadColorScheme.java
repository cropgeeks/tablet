// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;

import tablet.data.*;
import tablet.gui.*;

public abstract class ReadColorScheme
{
	public static final int STANDARD = 10;
	public static final int CLASSIC = 20;
	public static final int DIRECTION = 30;
	public static final int READTYPE = 40;


	/** Returns a DNA colouring scheme from the cache of schemes. */
	public static ReadColorScheme getScheme(int type, int w, int h)
	{
		// Ensure the correct type of pad character rendering is used
		if (Prefs.visPadCharType == 0)
			Sequence.PAD = "*";
		else if (Prefs.visPadCharType == 1)
			Sequence.PAD = "-";

		ReadColorScheme scheme = null;

		if (type == STANDARD)
			scheme = new StandardColorScheme(w, h, true, true);

		else if (type == CLASSIC)
			scheme = new TextColorScheme(w, h);

		else if (type == DIRECTION)
			scheme = new DirectionColorScheme(w, h);

		else if (type == READTYPE)
			scheme = new ReadTypeColorScheme(w, h);

		return scheme;
	}

	public abstract Image getConsensusImage(int data);

	public abstract Image getImage(ReadMetaData rmd, int index);

	public abstract Color getColor(ReadMetaData rmd, int index);

	public abstract Image getPairLink();
}