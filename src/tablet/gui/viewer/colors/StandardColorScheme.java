// Copyright 2009-2010 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import tablet.data.*;

public class StandardColorScheme extends ColorScheme
{
	// Holds the states needed by the reads canvas
	protected ArrayList<ColorState> statesRD = new ArrayList<ColorState>();
	// Holds the states needed by the consensus canvas
	protected ArrayList<ColorState> statesCS = new ArrayList<ColorState>();

	public StandardColorScheme(int w, int h, boolean csOnly)
	{
		super();

		createStatesCS(w, h);

		if (csOnly == false)
		{
			createStatesRD(w, h);

			// Duplicate the second half (to deal/ignore with orientation
			//  add 6 to get us up to "20"
			for (int i = 0; i < 6; i++)
				statesRD.add(null);
			//  then add the same pre-created states all over again
			for (int i = 0; i < 14; i++)
				statesRD.add(statesRD.get(i));
		}
	}

	private void createStatesCS(int w, int h)
	{
		// VERY IMPORTANT: These MUST be in the same order as the sequential
		// values within the data.Sequence class, eg, unknown, P, N, A, C, G, T

		// Sequence.UNKNOWN
		statesCS.add(new StandardColorState("?", Color.lightGray, w, h, false, false));
		statesCS.add(null);

		// Sequence.P
		statesCS.add(new StandardColorState(Sequence.PAD, Color.lightGray, w, h, false, false));
		statesCS.add(null);

		// Sequence.N
		statesCS.add(new StandardColorState("N", Color.lightGray, w, h, false, false));
		statesCS.add(null);

		// Sequence.A
		statesCS.add(new StandardColorState("A", new Color(120, 255, 120), w, h, false, false));
		statesCS.add(null);

		// Sequence.C
		statesCS.add(new StandardColorState("C", new Color(255, 160, 120), w, h, false, false));
		statesCS.add(null);

		// Sequence.G
		statesCS.add(new StandardColorState("G", new Color(255, 120, 120), w, h, false, false));
		statesCS.add(null);

		// Sequence.T
		statesCS.add(new StandardColorState("T", new Color(120, 120, 255), w, h, false, false));
		statesCS.add(null);
	}

	protected void createStatesRD(int w, int h)
	{
		// VERY IMPORTANT: These MUST be in the same order as the sequential
		// values within the data.Sequence class, eg, unknown, P, N, A, C, G, T

		// Sequence.UNKNOWN
		statesRD.add(new StandardColorState("?", Color.lightGray, w, h, true, false));
		statesRD.add(new StandardColorState("?", Color.lightGray, w, h, true, true));

		// Sequence.P
		statesRD.add(new StandardColorState(Sequence.PAD, Color.lightGray, w, h, true, false));
		statesRD.add(new StandardColorState(Sequence.PAD, Color.lightGray, w, h, true, true));

		// Sequence.N
		statesRD.add(new StandardColorState("N", Color.lightGray, w, h, true, false));
		statesRD.add(new StandardColorState("N", Color.lightGray, w, h, true, true));

		// Sequence.A
		statesRD.add(new StandardColorState("A", new Color(120, 255, 120), w, h, true, false));
		statesRD.add(new StandardColorState("A", new Color(120, 255, 120), w, h, true, true));

		// Sequence.C
		statesRD.add(new StandardColorState("C", new Color(255, 160, 120), w, h, true, false));
		statesRD.add(new StandardColorState("C", new Color(255, 160, 120), w, h, true, true));

		// Sequence.G
		statesRD.add(new StandardColorState("G", new Color(255, 120, 120), w, h, true, false));
		statesRD.add(new StandardColorState("G", new Color(255, 120, 120), w, h, true, true));

		// Sequence.T
		statesRD.add(new StandardColorState("T", new Color(120, 120, 255), w, h, true, false));
		statesRD.add(new StandardColorState("T", new Color(120, 120, 255), w, h, true, true));
	}

	public Image getImage(int data)
	{
		return statesRD.get(data).getImage();
	}

	public Image getConsensusImage(int data)
	{
		return statesCS.get(data).getImage();
	}

	public Color getColor(int data)
	{
		return statesRD.get(data).getColor();
	}
}