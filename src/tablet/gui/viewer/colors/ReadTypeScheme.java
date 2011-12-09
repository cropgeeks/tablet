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

		// Create six sets of colours for each type of read available
		Color cUnpaired = ColorPrefs.getColor("ReadTypeScheme.Unpaired", new Color(255, 160, 120));
		Color cFirstInP = ColorPrefs.getColor("ReadTypeScheme.FirstInP", new Color(120, 255, 120));
		Color cSecndInP = ColorPrefs.getColor("ReadTypeScheme.SecndInP", new Color(120, 120, 255));
		Color cOrphaned = ColorPrefs.getColor("ReadTypeScheme.Orphaned", new Color(255, 120, 120));
		Color cFInPDiff = ColorPrefs.getColor("ReadTypeScheme.FInPDiff", new Color(255, 255, 120));
		Color cSInPDiff = ColorPrefs.getColor("ReadTypeScheme.SInPDiff", new Color(255, 120, 255));

		initStates(unpaired, cUnpaired, w, h);
		initStates(firstInP, cFirstInP, w, h);
		initStates(secndInP, cSecndInP, w, h);
		initStates(orphaned, cOrphaned, w, h);
		initStates(fInPDiff, cFInPDiff, w, h);
		initStates(sInPDiff, cSInPDiff, w, h);
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