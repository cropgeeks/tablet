// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;

import tablet.analysis.*;
import tablet.data.*;
import tablet.gui.*;

public abstract class ProteinColorScheme
{
	/** Returns a PROTEIN colouring scheme from the cache of schemes. */
	public static ProteinColorScheme getScheme(int type, int w, int h)
	{
		// Ensure the correct type of pad character rendering is used
		if (Prefs.visStopCharType == 0)
			ProteinTranslator.setStopCharacter(".");
		else if (Prefs.visStopCharType == 1)
			ProteinTranslator.setStopCharacter("*");

		ProteinColorScheme scheme = null;

		if (type == ReadColorScheme.CLASSIC)
			scheme = new ProteinTextColorScheme(w, h);

		else
			scheme = new ProteinClassificationColorScheme(w, h);

		return scheme;
	}

	public abstract Image getImage(int data);
}