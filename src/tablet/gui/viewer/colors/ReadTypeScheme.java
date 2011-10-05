// Copyright 2009-2011 Plant Bioinformatics Group, SCRI. All rights reserved.
// Use is subject to the accompanying licence terms.

package tablet.gui.viewer.colors;

import java.awt.*;
import java.util.*;

import tablet.data.*;
import static tablet.data.ReadMetaData.*;

public class ReadTypeScheme extends EnhancedScheme
{
	private ArrayList<ColorStamp> unpaired = new ArrayList<ColorStamp>();
	private ArrayList<ColorStamp> firstInP = new ArrayList<ColorStamp>();
	private ArrayList<ColorStamp> secndInP = new ArrayList<ColorStamp>();
	private ArrayList<ColorStamp> orphaned = new ArrayList<ColorStamp>();
	private ArrayList<ColorStamp> fInPDiff = new ArrayList<ColorStamp>();
	private ArrayList<ColorStamp> sInPDiff = new ArrayList<ColorStamp>();

	public ReadTypeScheme(int w, int h)
	{
		super(w, h, true, false);

		// Create four sets of colours for each type of read available
		initStates(unpaired, new Color(255, 160, 120), w, h);
		initStates(firstInP, new Color(120, 255, 120), w, h);
		initStates(secndInP, new Color(120, 120, 255), w, h);
		initStates(orphaned, new Color(255, 120, 120), w, h);
		initStates(fInPDiff, new Color(255, 255, 120), w, h);
		initStates(sInPDiff, new Color(255, 120, 255), w, h);
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
		switch (rmd.getPairedType())
		{
			case FIRSTINP:
				return firstInP.get(rmd.getStateAt(index)).getImage();

			case SECNDINP:
				return secndInP.get(rmd.getStateAt(index)).getImage();

			case ORPHANED:
				return orphaned.get(rmd.getStateAt(index)).getImage();

			case DFRSTINP:
				return fInPDiff.get(rmd.getStateAt(index)).getImage();

			case DSCNDINP:
				return sInPDiff.get(rmd.getStateAt(index)).getImage();
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

			case DFRSTINP:
				return fInPDiff.get(rmd.getStateAt(index)).getColor();

			case DSCNDINP:
				return sInPDiff.get(rmd.getStateAt(index)).getColor();
		}

		return unpaired.get(rmd.getStateAt(index)).getColor();
	}
}