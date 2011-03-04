// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import tablet.data.*;

public class DirectionColorScheme extends StandardColorScheme
{
	private ArrayList<ColorState> statesF = new ArrayList<ColorState>();
	private ArrayList<ColorState> statesR = new ArrayList<ColorState>();

	public DirectionColorScheme(int w, int h)
	{
		super(w, h, true, false);

		// Create two sets of colours: one for each direction
		initStates(statesF, new Color(165, 200, 175), w, h);
		initStates(statesR, new Color(70, 116, 162), w, h);
	}

	private void initStates(ArrayList<ColorState> states, Color c, int w, int h)
	{
		for (String base: Sequence.getStates())
		{
			// Add the normal image
			states.add(new StandardColorState(base, c, w, h, true, false));
			// Add the delta image
			states.add(new StandardColorState(base, c, w, h, true, true));
		}
	}

	public Image getImage(ReadMetaData rmd, int index)
	{
		if (rmd.isComplemented())
			return statesR.get(rmd.getStateAt(index)).getImage();
		else
			return statesF.get(rmd.getStateAt(index)).getImage();
	}

	public Color getColor(ReadMetaData rmd, int index)
	{
		if (rmd.isComplemented())
			return statesR.get(rmd.getStateAt(index)).getColor();
		else
			return statesF.get(rmd.getStateAt(index)).getColor();
	}
}