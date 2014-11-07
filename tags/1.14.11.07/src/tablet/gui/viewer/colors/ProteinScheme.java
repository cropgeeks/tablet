// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;

import tablet.analysis.*;
import tablet.gui.*;

public abstract class ProteinScheme
{
	/** Returns a PROTEIN colouring scheme from the cache of schemes. */
	public static ProteinScheme getScheme(int type, int w, int h)
	{
		// Ensure the correct type of pad character rendering is used
		if (Prefs.visStopCharType == 0)
			ProteinTranslator.setStopCharacter(".");
		else if (Prefs.visStopCharType == 1)
			ProteinTranslator.setStopCharacter("*");

		return new ProteinEnhancedScheme(w, h);
	}

	public abstract Image getImage(int data);
}