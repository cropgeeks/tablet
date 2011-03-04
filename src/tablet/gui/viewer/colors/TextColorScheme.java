// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import tablet.data.*;

public class TextColorScheme extends StandardColorScheme
{
	public TextColorScheme(int w, int h)
	{
		super(w, h, false, false);

		// Create the images used by the consensus canvas
		for (String base: Sequence.getStates())
		{
			statesCS.add(new TextState(base, w, h, false, false));
			statesCS.add(null);
		}

		// Create the images used by the main (reads) canvas
		for (String base: Sequence.getStates())
		{
			statesRD.add(new TextState(base, w, h, true, false));
			statesRD.add(new TextState(base, w, h, true, true));
		}
	}
}