// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;

import tablet.data.*;

public class ReadTypeColorScheme extends StandardColorScheme
{
	public ReadTypeColorScheme(int w, int h)
	{
		super(w, h, true);

		// VERY IMPORTANT: These MUST be in the same order as the sequential
		// values within the data.Sequence class, eg, unknown, P, N, A, C, G, T

		// Three sets of states: Normal Read, 1st in Pair, 2nd in Pair
		for (int i = 0; i < 4; i++)
		{
			Color c = null;

			switch (i)
			{
				case 0: c = new Color(255, 160, 120); break;
				case 1: c = new Color(120, 255, 120); break;
				case 2: c = new Color(120, 120, 255); break;
				case 3: c = new Color(255, 120, 120); break;
			}


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

			// Pair Link
			statesRD.add(new PairLinkColorState(new Color(180, 180, 180), w, h));


			// Pad out to start the next set at 16
			for (int j = 0; j < 1 && i < 3; j++)
				statesRD.add(null);
		}
	}
}