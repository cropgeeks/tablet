// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import tablet.data.*;

public class ClassicScheme extends EnhancedScheme
{
	public ClassicScheme(int w, int h)
	{
		super(w, h, false, false);

		// Create the images used by the consensus canvas
		for (String base: Sequence.getStates())
		{
			statesCS.add(new GrayscaleStamp(base, w, h, false, false));
			statesCS.add(null);
		}

		// Create the images used by the main (reads) canvas
		for (String base: Sequence.getStates())
		{
			statesRD.add(new GrayscaleStamp(base, w, h, true, false));
			statesRD.add(new GrayscaleStamp(base, w, h, true, true));
		}
	}
}