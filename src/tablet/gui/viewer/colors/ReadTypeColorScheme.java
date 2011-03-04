// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.util.*;

import tablet.data.*;
import static tablet.data.ReadMetaData.*;

public class ReadTypeColorScheme extends StandardColorScheme
{
	private ArrayList<ColorState> unpaired = new ArrayList<ColorState>();
	private ArrayList<ColorState> firstInP = new ArrayList<ColorState>();
	private ArrayList<ColorState> secndInP = new ArrayList<ColorState>();
	private ArrayList<ColorState> orphaned = new ArrayList<ColorState>();

	public ReadTypeColorScheme(int w, int h)
	{
		super(w, h, true, false);

		// Create four sets of colours for each type of read available
		initStates(unpaired, new Color(255, 160, 120), w, h);
		initStates(firstInP, new Color(120, 255, 120), w, h);
		initStates(secndInP, new Color(120, 120, 255), w, h);
		initStates(orphaned, new Color(255, 120, 120), w, h);
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
		switch (rmd.getPairedType())
		{
			case FIRSTINP:
				return firstInP.get(rmd.getStateAt(index)).getImage();

			case SECNDINP:
				return secndInP.get(rmd.getStateAt(index)).getImage();

			case ORPHANED:
				return orphaned.get(rmd.getStateAt(index)).getImage();
		}

		return unpaired.get(rmd.getStateAt(index)).getImage();
	}

	public Color getColor(ReadMetaData rmd, int index)
	{
		switch (rmd.getPairedType())
		{
			case FIRSTINP:
				return firstInP.get(rmd.getStateAt(index)).getColor();

			case SECNDINP:
				return secndInP.get(rmd.getStateAt(index)).getColor();

			case ORPHANED:
				return orphaned.get(rmd.getStateAt(index)).getColor();
		}

		return unpaired.get(rmd.getStateAt(index)).getColor();
	}
}