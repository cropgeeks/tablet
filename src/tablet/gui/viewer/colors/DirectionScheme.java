// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.util.*;

import tablet.data.*;

public class DirectionScheme extends EnhancedScheme
{
	private ArrayList<ColorStamp> statesF = new ArrayList<ColorStamp>();
	private ArrayList<ColorStamp> statesR = new ArrayList<ColorStamp>();

	public DirectionScheme(int w, int h)
	{
		super(w, h, true, false);

		// Create two sets of colours: one for each direction
		initStates(statesF, new Color(165, 200, 175), w, h);
		initStates(statesR, new Color(70, 116, 162), w, h);
	}

	private void initStates(ArrayList<ColorStamp> states, Color c, int w, int h)
	{
		for (String base: Sequence.getStates())
		{
			// Add the normal image
			states.add(new ColorStamp(base, c, w, h, true, false));
			// Add the delta image
			states.add(new ColorStamp(base, c, w, h, true, true));
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