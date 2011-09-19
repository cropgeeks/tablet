// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import javax.swing.SwingUtilities;

import tablet.data.*;
import tablet.gui.*;
import tablet.gui.ribbon.RibbonController;

public abstract class ReadScheme
{
	public static final int STANDARD = 10;
	public static final int CLASSIC = 20;
	public static final int DIRECTION = 30;
	public static final int READTYPE = 40;
	public static final int READGROUP = 50;
	public static final int READLENGTH = 60;


	/** Returns a DNA colouring scheme from the cache of schemes. */
	public static ReadScheme getScheme(int type, int w, int h)
	{
		// Ensure the correct type of pad character rendering is used
		if (Prefs.visPadCharType == 0)
			Sequence.PAD = "*";
		else if (Prefs.visPadCharType == 1)
			Sequence.PAD = "-";

		ReadScheme scheme = null;

		if (type == STANDARD)
			scheme = new EnhancedScheme(w, h, true, true);

		else if (type == CLASSIC)
			scheme = new ClassicScheme(w, h);

		else if (type == DIRECTION)
			scheme = new DirectionScheme(w, h);

		else if (type == READTYPE)
			scheme = new ReadTypeScheme(w, h);

		else if (type == READGROUP)
			scheme = new ReadGroupScheme(w, h);

		else if (type == READLENGTH)
			scheme = new ReadLengthScheme(w, h);

		return scheme;
	}

	public abstract Image getConsensusImage(int data);

	public abstract Image getImage(ReadMetaData rmd, int index);

	public abstract Color getColor(ReadMetaData rmd, int index);

	public abstract Image getPairLink();
}