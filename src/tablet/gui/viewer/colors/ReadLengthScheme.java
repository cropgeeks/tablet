// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.util.*;

import tablet.data.*;

public class ReadLengthScheme extends EnhancedScheme
{
	private ArrayList<ArrayList<ColorStamp>> states = new ArrayList<ArrayList<ColorStamp>>();

	public ReadLengthScheme(int w, int h)
	{
		super(w, h, true, false);

		Color c50  = ColorPrefs.getColor("ReadLengthScheme.50",  new Color(120, 255, 120));
		Color c100 = ColorPrefs.getColor("ReadLengthScheme.100", new Color(255, 160, 120));
		Color c150 = ColorPrefs.getColor("ReadLengthScheme.150", new Color(255, 120, 120));
		Color c250 = ColorPrefs.getColor("ReadLengthScheme.250", new Color(120, 120, 255));
		Color c500 = ColorPrefs.getColor("ReadLengthScheme.500", new Color(165, 200, 175));
		Color c750 = ColorPrefs.getColor("ReadLengthScheme.750", new Color(070, 116, 162));
		Color cOth = ColorPrefs.getColor("ReadLengthScheme.Other", new Color(116, 162, 070));

		initStates(c50, w, h);
		initStates(c100, w, h);
		initStates(c150, w, h);
		initStates(c250, w, h);
		initStates(c500, w, h);
		initStates(c750, w, h);
		initStates(cOth, w, h);
	}

	private void initStates(Color c, int w, int h)
	{
		ArrayList<ColorStamp> list = new ArrayList<ColorStamp>();

		for (String base: Sequence.getStates())
		{
			// Add the normal image
			list.add(new ColorStamp(base, c, w, h, true, false));
			// Add the delta image
			list.add(new ColorStamp(base, c, w, h, true, true));
		}

		states.add(list);
	}

	public Image getImage(ReadMetaData rmd, int index)
	{
		int bin = rmd.getLengthBin();
		return states.get(bin).get(rmd.getStateAt(index)).getImage();
	}

	public Color getColor(ReadMetaData rmd, int index)
	{
		int bin = rmd.getLengthBin();
		return states.get(bin).get(rmd.getStateAt(index)).getColor();
	}

	public static int getBin(int length)
	{
		if (length < 50)
			return 0;
		else if (length < 100)
			return 1;
		else if (length < 150)
			return 2;
		else if (length < 250)
			return 3;
		else if (length < 500)
			return 4;
		else if (length < 750)
			return 5;
		else
			return 6;
	}
}