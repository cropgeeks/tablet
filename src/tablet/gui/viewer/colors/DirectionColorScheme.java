// Copyright 2009 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import tablet.data.*;

public class DirectionColorScheme extends StandardColorScheme
{
	public DirectionColorScheme(int w, int h)
	{
		super(w, h, true);

		// VERY IMPORTANT: These MUST be in the same order as the sequential
		// values within the data.Sequence class, eg, unknown, P, N, A, C, G, T

		// Do twice, once for forward, then again for reverse
		for (int i = 0; i < 2; i++)
		{
			Color c = (i == 0) ?
				new Color(165, 200, 175) : new Color(70, 116, 162);


			// Sequence.UNKNOWN
			statesRD.add(new StandardColorState("?", c, w, h, true, false));
			statesRD.add(new StandardColorState("?", c, w, h, true, true));

			// Sequence.P
			statesRD.add(new StandardColorState(Sequence.PAD, c, w, h, true, false));
			statesRD.add(new StandardColorState(Sequence.PAD, c, w, h, true, true));

			// Sequence.N
			statesRD.add(new StandardColorState("N", c, w, h, true, false));
			statesRD.add(new StandardColorState("N", c, w, h, true, true));

			// Sequence.A
			statesRD.add(new StandardColorState("A", c, w, h, true, false));
			statesRD.add(new StandardColorState("A", c, w, h, true, true));

			// Sequence.C
			statesRD.add(new StandardColorState("C", c, w, h, true, false));
			statesRD.add(new StandardColorState("C", c, w, h, true, true));

			// Sequence.G
			statesRD.add(new StandardColorState("G", c, w, h, true, false));
			statesRD.add(new StandardColorState("G", c, w, h, true, true));

			// Sequence.T
			statesRD.add(new StandardColorState("T", c, w, h, true, false));
			statesRD.add(new StandardColorState("T", c, w, h, true, true));

			for (int j = 0; j < 6 && i == 0; j++)
				statesRD.add(null);
		}
	}
}